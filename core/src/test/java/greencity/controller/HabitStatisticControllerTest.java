package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.user.UserVO;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @InjectMocks
    private HabitStatisticController habitStatisticController;

    private final Principal principal = getPrincipal();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
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
}