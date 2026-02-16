package greencity.notification.event;

import greencity.enums.NotificationAction;
import java.time.LocalDateTime;

public interface NotificationEvent {
    Long recipientUserId();

    Long actorUserId();

    NotificationAction actionType();

    String objectType();

    Long objectId();

    String objectTitle();

    default LocalDateTime occurredAt() {
        return LocalDateTime.now();
    }
}