package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.*;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitStatisticControllerTest {
    private static final String habitStatisticControllerLink = "/habit/statistic";
    private MockMvc mockMvc;

    @Mock
    private HabitStatisticService habitStatisticService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ErrorAttributes errorAttributes;

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    private final Principal principal = getPrincipal();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @BeforeEach
    void setup() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(new HashMap<>(Map.of(
                        "message", "error",
                        "timestamp", "2026-01-06T20:00:00Z",
                        "path", "/habit/statistic",
                        "trace", "stacktrace"
                )));

        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void findAllByHabitId() throws Exception {
        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", 1L))
                .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitId(1L);
    }

    @Test
    void findAllStatsByHabitAssignId() throws Exception {
        mockMvc.perform(get(habitStatisticControllerLink + "/assign/{habitAssignId}", 1L))
                .andExpect(status().isOk());

        verify(habitStatisticService).findAllStatsByHabitAssignId(1L);
    }

    @Test
    void saveHabitStatistic() throws Exception {
        UserVO userVO = getUserVO();
        AddHabitStatisticDto addDto = addHabitStatisticDto();
        String content = objectMapper.writeValueAsString(addDto);

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        mockMvc.perform(post(habitStatisticControllerLink + "/{habitId}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        verify(userService).findByEmail("test@gmail.com");
    }

    @Test
    void updateStatistic() throws Exception {
        UserVO userVO = getUserVO();

        UpdateHabitStatisticDto updateDto = UpdateHabitStatisticDto.builder()
                .amountOfItems(10)
                .habitRate(HabitRate.GOOD)
                .build();

        String content = objectMapper.writeValueAsString(updateDto);

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        mockMvc.perform(put(habitStatisticControllerLink + "/{id}", 1L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        verify(habitStatisticService).update(1L, userVO.getId(), updateDto);
    }

    @Test
    void getTodayStatisticsForAllHabitItems() throws Exception {
        String language = "en";
        List<HabitItemsAmountStatisticDto> dtoList = Collections.emptyList();

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(language)).thenReturn(dtoList);

        mockMvc.perform(get(habitStatisticControllerLink + "/todayStatisticsForAllHabitItems")
                        .locale(Locale.ENGLISH))
                .andExpect(status().isOk());

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(language);
    }

    @Test
    void findAmountOfAcquiredHabits() throws Exception {
        Long userId = 1L;
        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(10L);

        mockMvc.perform(get(habitStatisticControllerLink + "/acquired/count")
                        .param("userId", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    void findAmountOfHabitsInProgress() throws Exception {
        Long userId = 1L;
        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(5L);

        mockMvc.perform(get(habitStatisticControllerLink + "/in-progress/count")
                        .param("userId", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(userId);
    }


    @Test
    void findAllByHabitId_NotFound() throws Exception {
        when(habitStatisticService.findAllStatsByHabitId(anyLong()))
                .thenThrow(new NotFoundException("Habit not found"));

        mockMvc.perform(get(habitStatisticControllerLink + "/{habitId}", 1L))
                .andExpect(status().isNotFound());

        verify(habitStatisticService).findAllStatsByHabitId(1L);
    }
}