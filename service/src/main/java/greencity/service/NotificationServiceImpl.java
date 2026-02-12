package greencity.service;

import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final NotificationDtoResponseMapper notificationDtoResponseMapper;
    private final RestClient restClient;

    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, Pageable page) {
        Page<Notification> notifications = notificationRepo
                .findAllByRecipientUserIdOrderByCreatedAtDesc(userId, page);

        if (notifications.isEmpty()) {
            return new PageableDto<>(Collections.emptyList(), 0, 0, 0);
        }

        Set<Long> actorIds = notifications.getContent()
                .stream()
                .map(Notification::getActorUserId)
                .collect(Collectors.toSet());

        final Map<Long, String> actorNames = getActorNamesSafety(actorIds);

        List<NotificationResponseDto> responseDtoList = notifications
                .getContent()
                .stream()
                .map(notification ->
                        notificationDtoResponseMapper
                                .convert(notification,
                                        actorNames.getOrDefault(
                                                notification.getActorUserId(), "Unknown user")
                                )
                )
                .toList();

        return new PageableDto<>(
                responseDtoList,
                notifications.getTotalElements(),
                notifications.getNumber(),
                notifications.getTotalPages());
    }

    private Map<Long, String> getActorNamesSafety(Set<Long> actorIds) {

        try {
            return restClient.findUserNamesByUserIds(actorIds);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}