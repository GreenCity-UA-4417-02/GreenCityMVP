package greencity.notifications.handler;

import greencity.entity.Notification;
import greencity.notification.event.NotificationEvent;

public interface NotificationEventHandler<E extends NotificationEvent> {
    Class<E> eventType();

    Notification toEntity(E event);
}