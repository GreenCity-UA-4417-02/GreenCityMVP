package greencity.controller;


import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitFactControllerTest {
    private static final String habitFactControllerLink = "/facts";

    private MockMvc mockMvc;

    @InjectMocks
    private HabitFactController habitFactController;

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(habitFactController)
                .setValidator(new Validator() {
                    @Override
                    public boolean supports(Class<?> clazz) {
                        return true;
                    }

                    @Override
                    public void validate(Object target, org.springframework.validation.Errors errors) {

                    }
                })
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
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
}
