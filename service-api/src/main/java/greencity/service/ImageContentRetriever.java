package greencity.service;

import greencity.dto.event.EventImageContentDto;

import java.util.UUID;

public interface ImageContentRetriever {
    EventImageContentDto getImageContent(UUID id);
}
