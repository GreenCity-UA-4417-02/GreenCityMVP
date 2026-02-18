package greencity.dto.notification;

import greencity.enums.NotificationAction;
import greencity.enums.NotificationOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class NotificationResponseDto {
    private String actorName;
    private NotificationAction action;
    private String objectTitle;
    private LocalDateTime createdAt;
    private boolean read;
    private NotificationOrigin origin;
}
