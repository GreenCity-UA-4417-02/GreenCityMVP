package greencity.notification.listener;

import greencity.entity.Notification;
import greencity.notification.event.NotificationEvent;
import greencity.notification.handler.NotificationEventHandler;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationEventListenerTest {
    @Mock
    NotificationRepo notificationRepo;
    @Mock
    NotificationEventHandler<NotificationEvent> notificationEventHandler;
    private NotificationEventListener notificationEventListener;

    @BeforeEach
    void setUp() {
        notificationEventListener = new NotificationEventListener(List.of(notificationEventHandler), notificationRepo);
    }

    @Test
    void shouldProcessNotificationEventAndSaveNotification() {
        NotificationEvent event = mock(NotificationEvent.class);
        Notification notification = mock(Notification.class);

        when(notificationEventHandler.eventType()).thenReturn((Class) event.getClass());
        when(notificationEventHandler.toEntity(event)).thenReturn(notification);

        notificationEventListener.handleNotification(event);

        verify(notificationEventHandler).toEntity(event);
        verify(notificationRepo).save(notification);
    }
}
