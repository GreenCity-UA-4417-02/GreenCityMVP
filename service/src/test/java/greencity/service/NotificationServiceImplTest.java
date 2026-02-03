package greencity.service;

import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static greencity.enums.NotificationAction.LIKED;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    NotificationRepo notificationRepo;

    @Mock
    NotificationDtoResponseMapper notificationDtoResponseMapper;

    @Mock
    RestClient restClient;

    @InjectMocks
    NotificationServiceImpl notificationServiceImpl;

    @Test
    void getAllNotificationsForUser_isOk() throws Exception {

        Long userId = 1L;
        String actorName = "John";
        Pageable pageable = PageRequest.of(0, 20);
        Notification notification = createNotification(1L, 2L);

        NotificationResponseDto dto = createNotificationResponseDto(actorName);

        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));

        when(notificationRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(page);
        when(restClient.findById(2L)).thenReturn(
                UserVO.builder()
                        .id(2L)
                        .name("John").build());
        when(notificationDtoResponseMapper.convert(notification, "John")).thenReturn(dto);

        PageableDto<NotificationResponseDto> allNotification = notificationServiceImpl.getAllNotificationsForUser(userId, pageable);

        assertEquals(1, allNotification.getPage().size());
        assertEquals(1, allNotification.getTotalElements());
    }

    @Test
    void getAllNotificationsForUser_whenActorNotFound_shouldReturnMessage() throws Exception {

        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Notification notification = createNotification(userId, 2L);

        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));

        when(notificationRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(page);
        when(restClient.findById(2L)).thenThrow(new RuntimeException("Unknown user"));
        when(notificationDtoResponseMapper.convert(any(), any())).thenReturn(createNotificationResponseDto("any"));

        PageableDto<NotificationResponseDto> allNotification = notificationServiceImpl.getAllNotificationsForUser(userId, pageable);

        ArgumentCaptor<String> actorNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationDtoResponseMapper).convert(eq(notification), actorNameCaptor.capture());

        assertEquals("Unknown user", actorNameCaptor.getValue());
        assertEquals(1, allNotification.getPage().size());
    }

    private Notification createNotification(Long recipientUserId, Long actorUserId) {
        return Notification.builder()
                .id(1L)
                .recipientUserId(recipientUserId)
                .actorUserId(actorUserId)
                .objectTitle("Open new vegetarian cafe")
                .createdAt(now())
                .build();
    }

    private NotificationResponseDto createNotificationResponseDto(String actorName) {
        return NotificationResponseDto.builder()
                .actorName(actorName)
                .action(LIKED)
                .objectTitle("Open new vegetarian cafe")
                .createdAt(now())
                .read(false)
                .build();
    }
}
