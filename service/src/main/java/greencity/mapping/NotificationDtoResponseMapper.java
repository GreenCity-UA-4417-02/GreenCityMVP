package greencity.mapping;

import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoResponseMapper {
    public NotificationResponseDto convert(Notification notification, String actorName) {
        return NotificationResponseDto.builder()
            .actorName(actorName)
            .action(notification.getNotificationAction())
            .objectTitle(notification.getObjectTitle())
            .createdAt(notification.getCreatedAt())
            .read(notification.getReadAt() != null)
            .build();
    }
}
