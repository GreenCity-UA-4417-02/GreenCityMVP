package greencity.entity;

import greencity.enums.NotificationAction;
import greencity.enums.NotificationOrigin;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientUserId;

    @Column(nullable = false)
    private Long actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationAction notificationAction;

    @Column(nullable = false)
    private String objectType;

    @Column(nullable = false)
    private Long objectId;

    @Column(nullable = false)
    private String objectTitle;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @Column(name = "origin", nullable = false, columnDefinition = "varchar(255) default 'GREEN_CITY'")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationOrigin origin = NotificationOrigin.GREEN_CITY;
}
