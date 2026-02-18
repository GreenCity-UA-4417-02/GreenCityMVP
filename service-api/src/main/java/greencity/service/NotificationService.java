package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import org.springframework.data.domain.Pageable;
import greencity.enums.NotificationOrigin;


public interface NotificationService {

    PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, NotificationOrigin origin, Pageable page);
}
