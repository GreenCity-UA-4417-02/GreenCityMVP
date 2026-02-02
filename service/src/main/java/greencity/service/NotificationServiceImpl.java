package greencity.service;

import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.mapping.NotificationDtoResponseMapper;
import greencity.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final NotificationDtoResponseMapper notificationDtoResponseMapper;
    private final RestClient restClient;

    public PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, Pageable page) {
        Page<Notification> notifications = notificationRepo.findAllByRecipientUserIdOrderByCreatedAtDesc(userId, page);
        Map<Long, String> actorNames = new HashMap<>();

        for (Notification notification : notifications.getContent()) {
            Long actorId = notification.getActorUserId();

            if (!actorNames.containsKey(actorId)) {
                String actorName;
                try {
                    UserVO actor = restClient.findById(actorId);
                    actorName = actor.getName();
                } catch (Exception e) {
                    actorName = "Unknown user";
                }
                actorNames.put(actorId, actorName);
            }
        }
        List<NotificationResponseDto> responseDtoList = notifications
                .getContent()
                .stream()
                .map(notification ->
                        notificationDtoResponseMapper.convert(notification, actorNames.get(notification.getActorUserId())
                        )
                )
                .toList();

        return new PageableDto<>(
                responseDtoList,
                notifications.getTotalElements(),
                notifications.getNumber(),
                notifications.getTotalPages());
    }
}
