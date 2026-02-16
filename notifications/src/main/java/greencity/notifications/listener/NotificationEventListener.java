package greencity.notifications.listener;

import greencity.entity.Notification;
import greencity.notification.event.NotificationEvent;
import greencity.notifications.handler.NotificationEventHandler;
import greencity.repository.NotificationRepo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;

@Component
public class NotificationEventListener {
    public final List<NotificationEventHandler<? extends NotificationEvent>> handlers;
    private final NotificationRepo notificationRepo;

    public NotificationEventListener(
            List<NotificationEventHandler<? extends NotificationEvent>> handlers,
            NotificationRepo notificationRepo) {
        this.handlers = handlers;
        this.notificationRepo = notificationRepo;
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationEvent event) {
        handlers.stream()
                .filter(handler -> handler.eventType().isAssignableFrom(event.getClass()))
                .findFirst()
                .ifPresent(handler -> process(handler, event));
    }

    @SuppressWarnings("unchecked")
    private <E extends NotificationEvent> void process(
            NotificationEventHandler<E> handler,
            NotificationEvent event) {
        E castedEvent = (E) event;
        Notification notification = handler.toEntity(castedEvent);
        notificationRepo.save(notification);
    }
}