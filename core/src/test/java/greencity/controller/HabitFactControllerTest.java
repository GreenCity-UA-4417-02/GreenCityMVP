package greencity.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitFactControllerTest {

    private static final String habitFactControllerLink = "/facts";
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @InjectMocks
    private HabitFactController habitFactController;
    @Mock
    private HabitFactService habitFactService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(habitFactController)
                .setControllerAdvice(
                        new CustomExceptionHandler(errorAttributes, objectMapper))
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    void getRandomFactByHabitId_shouldReturn200() throws Exception {
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();

        Long habitId = 1L;
        String lang = "en";

        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, lang))
                .thenReturn(languageTranslationDTO);

        mockMvc.perform(
                get(habitFactControllerLink + "/random/{habitId}", habitId)
                        .header("Accept-Language", lang)
        ).andExpect(status().isOk());

        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(habitId, lang);
    }

    @Test
    void getHabitFactOfTheDay_shouldReturn200() throws Exception {
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();

        Long languageId = 1L;

        when(habitFactService.getHabitFactOfTheDay(languageId))
                .thenReturn(languageTranslationDTO);

        mockMvc.perform(
                get(habitFactControllerLink + "/dayFact/{languageId}", languageId)
        ).andExpect(status().isOk());

        verify(habitFactService).getHabitFactOfTheDay(languageId);
    }

    @Test
    void getAllHabitFacts_shouldReturn200() throws Exception {
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO();
        var list = List.of(languageTranslationDTO);
        var pageableDto = new PageableDto<>(list, 1L, 0, 1);

        String lang = "en";

        when(habitFactService.getAllHabitFacts(any(Pageable.class), eq(lang)))
                .thenReturn(pageableDto);

        mockMvc.perform(
                get(habitFactControllerLink)
                        .header("Accept-Language", lang)
        ).andExpect(status().isOk());

        verify(habitFactService).getAllHabitFacts(any(Pageable.class), eq(lang));
    }

    @Test
    void saveHabitFact_shouldReturn201() throws Exception {
        String content = """
                {
                "translations": [
                    {
                        "language": {
                         "id": 1,
                         "code": "en"
                         },
                        "content": "Use public transport"
                    }
                ],
                "habit": {
                    "id": 1
                    }
                }
                """;
        HabitFactVO habitFactVo = new HabitFactVO();
        HabitFactDtoResponse responseDto = new HabitFactDtoResponse();

        when(habitFactService.save(any(HabitFactPostDto.class)))
                .thenReturn(habitFactVo);

        when(modelMapper.map(any(HabitFactVO.class), eq(HabitFactDtoResponse.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post(habitFactControllerLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isCreated());

        verify(habitFactService).save(any(HabitFactPostDto.class));
        verify(modelMapper).map(habitFactVo, HabitFactDtoResponse.class);
    }

    @Test
    void updateHabitFact_shouldReturn200() throws Exception {
        String content = """
                {
                    "translations": [
                        {
                        "factOfDayStatus": "POTENTIAL",
                        "language": {
                            "id": 1,
                            "code": "en"
                            },
                        "content": "some content"
                        }
                    ],
                    "habit": {
                        "id": 1
                    }
                }
                """;
        HabitFactVO habitFactVo = new HabitFactVO();
        HabitFactPostDto habitFactPostDto = new HabitFactPostDto();

        Long id = 1L;

        when(habitFactService.update(any(HabitFactUpdateDto.class), eq(id)))
                .thenReturn(habitFactVo);

        when(modelMapper.map(any(HabitFactVO.class), eq(HabitFactPostDto.class)))
                .thenReturn(habitFactPostDto);
        mockMvc.perform(
                        put(habitFactControllerLink + "/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isOk());

        verify(habitFactService).update(any(HabitFactUpdateDto.class), eq(id));
        verify(modelMapper).map(habitFactVo, HabitFactPostDto.class);
    }

    @Test
    void deleteHabitFact_shouldReturn200() throws Exception {

        Long id = 1L;

        mockMvc.perform(
                        delete(habitFactControllerLink + "/{id}", id))
                .andExpect(status().isOk());

        verify(habitFactService).delete(id);
    }

    @Test
    void deleteHabitFact_shouldReturn400_whenHabitFactNotFound() throws Exception {

        Long id = 999L;
        doThrow(new NotDeletedException(ErrorMessage.HABIT_FACT_NOT_DELETED_BY_ID))
                .when(habitFactService).delete(id);

        mockMvc.perform(
                        delete(habitFactControllerLink + "/{id}", id))
                .andExpect(status().isBadRequest());

        verify(habitFactService).delete(id);
    }
}
