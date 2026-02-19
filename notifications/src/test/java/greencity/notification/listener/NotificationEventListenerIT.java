package greencity.notification.listener;

import greencity.entity.Notification;
import greencity.enums.NotificationAction;
import greencity.notification.event.NotificationEvent;
import greencity.notification.handler.NotificationEventHandler;
import greencity.repository.NotificationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Import({NotificationEventListener.class, NotificationEventListenerIT.TestConfig.class})
public class NotificationEventListenerIT {
    @Autowired
    NotificationRepo notificationRepo;
    @Autowired
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void clean() {
        notificationRepo.deleteAll();
    }

    @Test
    @Transactional
    @Commit
    void shouldCreateNotificationAfterCommit() {
        publisher.publishEvent(new TestEvent());

        TestTransaction.flagForCommit();
        TestTransaction.end();

        List<Notification> all = notificationRepo.findAll();
        assertFalse(all.isEmpty());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Notification.class)
    @EnableJpaRepositories(basePackageClasses = NotificationRepo.class)
    static class TestApp {

    }

    static class TestConfig {

        @Bean
        NotificationEventHandler<TestEvent> testHandler() {
            return new NotificationEventHandler<>() {
                @Override
                public Class<TestEvent> eventType() {
                    return TestEvent.class;
                }

                @Override
                public Notification toEntity(TestEvent event) {
                    return Notification.builder()
                            .recipientUserId(1L)
                            .actorUserId(2L)
                            .notificationAction(NotificationAction.LIKED)
                            .objectType("test")
                            .objectId(1L)
                            .objectTitle("Test title")
                            .createdAt(LocalDateTime.now())
                            .build();
                }
            };
        }
    }

    static class TestEvent implements NotificationEvent {
        public Long recipientUserId() {
            return 1L;
        }

        public Long actorUserId() {
            return 2L;
        }

        public NotificationAction actionType() {
            return NotificationAction.LIKED;
        }

        public String objectType() {
            return "test";
        }

        public Long objectId() {
            return 1L;
        }

        public String objectTitle() {
            return "Test title";
        }
    }
}
