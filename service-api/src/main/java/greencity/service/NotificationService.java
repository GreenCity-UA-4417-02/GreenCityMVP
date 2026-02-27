package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import org.springframework.lang.Nullable;
import org.springframework.data.domain.Pageable;
import greencity.enums.NotificationOrigin;


public interface NotificationService {

    PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, @Nullable NotificationOrigin origin, Pageable page);
}
