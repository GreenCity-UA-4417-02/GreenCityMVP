package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    PageableDto<NotificationResponseDto> getAllNotificationsForUser(Long userId, Pageable page);
}