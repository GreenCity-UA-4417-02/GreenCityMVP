package greencity.service;

import greencity.dto.event.EventDto;

public interface EmailNotificationService {
    void sendNotification(EventDto eventDto);
}
