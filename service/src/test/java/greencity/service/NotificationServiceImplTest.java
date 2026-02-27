package greencity.service;

import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.enums.NotificationOrigin;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static greencity.enums.NotificationAction.LIKED;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
        Notification notification = createNotification(userId, 2L);

        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));

        Map<Long, String> actorsName = Map.of(2L, actorName);
        NotificationResponseDto dto = createNotificationResponseDto(actorName);

        when(notificationRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(userId, pageable))
                .thenReturn(page);
        when(restClient.findUserNamesByUserIds(Set.of(2L))).thenReturn(actorsName);
        when(notificationDtoResponseMapper.convert(notification, actorName)).thenReturn(dto);

        PageableDto<NotificationResponseDto> allNotification = notificationServiceImpl
                .getAllNotificationsForUser(userId, null, pageable);

        assertEquals(1, allNotification.getPage().size());
        assertEquals("John", allNotification.getPage().getFirst().getActorName());
    }

    @Test
    void getAllNotificationsForUser_whenActorNotFound_shouldReturnMessage() throws Exception {

        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        Notification notification = createNotification(userId, 2L);

        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));

        when(notificationRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(page);
        when(restClient.findUserNamesByUserIds(Set.of(2L))).thenReturn(Collections.emptyMap());
        when(notificationDtoResponseMapper.convert(notification, "Unknown user"))
                .thenReturn(createNotificationResponseDto("Unknown user"));

        PageableDto<NotificationResponseDto> allNotification = notificationServiceImpl
                .getAllNotificationsForUser(userId, null, pageable);

        assertEquals("Unknown user", allNotification.getPage().getFirst().getActorName());
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

    @Test
    void getAllNotificationsForUser_withOrigin_ShouldFilterByOrigin() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);
        NotificationOrigin origin = NotificationOrigin.GREEN_CITY;
        Notification notification = createNotification(userId, 2L);
        notification.setOrigin(origin);

        Page<Notification> page = new PageImpl<>(Collections.singletonList(notification));

        when(notificationRepo.findAllByRecipientUserIdAndOriginOrderByCreatedAtDesc(userId, origin, pageable))
                .thenReturn(page);
        when(restClient.findUserNamesByUserIds(anySet())).thenReturn(Collections.emptyMap());
        when(notificationDtoResponseMapper.convert(any(Notification.class), anyString()))
                .thenReturn(NotificationResponseDto.builder().origin(origin).build());

        var result = notificationServiceImpl.getAllNotificationsForUser(userId, origin, pageable);

        assertEquals(1, result.getPage().size());
        assertEquals(origin, result.getPage().getFirst().getOrigin());
        verify(notificationRepo).findAllByRecipientUserIdAndOriginOrderByCreatedAtDesc(userId, origin, pageable);
    }
}
