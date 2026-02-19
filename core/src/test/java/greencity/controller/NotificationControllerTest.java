package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationAction;
import greencity.service.NotificationService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationControllerTest {

    private static final String NOTIFICATIONS_CONTROLLER_LINK = "/notifications";

    private final Principal principal = getPrincipal();
    private MockMvc mockMvc;

    @InjectMocks
    private NotificationController notificationController;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(notificationController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }

    @Test
    void getAllNotificationsForUser_isOk() throws Exception {

        UserVO userVO = getUserVO();
        NotificationResponseDto dto = new NotificationResponseDto(
            "Anna",
            NotificationAction.COMMENTED,
            "Open new cafe",
            now(),
            false);
        PageableDto<NotificationResponseDto> pageableDto = new PageableDto<>(List.of(dto), 1, 0, 1);

        when(userService.findByEmail("test@gmail.com")).thenReturn(userVO);
        when(notificationService.getAllNotificationsForUser(eq(userVO.getId()), any(Pageable.class)))
            .thenReturn(pageableDto);

        mockMvc.perform(get(NOTIFICATIONS_CONTROLLER_LINK)
            .principal(principal)
            .param("page", "0")
            .param("size", "20"))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(status().isOk());

        verify(notificationService).getAllNotificationsForUser(
            eq(userVO.getId()),
            any(Pageable.class));
    }

    @Test
    void getAllNotificationsForUser_isEmpty() throws Exception {

        UserVO userVO = getUserVO();
        PageableDto<NotificationResponseDto> pageableDto = new PageableDto<>(List.of(), 0, 0, 0);

        when(userService.findByEmail("test@gmail.com")).thenReturn(userVO);
        when(notificationService.getAllNotificationsForUser(any(), any())).thenReturn(pageableDto);

        mockMvc.perform(get(NOTIFICATIONS_CONTROLLER_LINK)
            .principal(principal)
            .param("page", "0")
            .param("size", "20"))
            .andExpect(jsonPath("$.page").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(status().isOk());

        verify(notificationService).getAllNotificationsForUser(
            eq(userVO.getId()),
            any(Pageable.class));
    }
}
