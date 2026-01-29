package greencity.service;

import greencity.dto.event.EventDto;

public interface EmailNotificationService {
    void sendDeleteNotification(EventDto eventDto);
    void sendUpdateNotification(EventDto eventDto);
}
