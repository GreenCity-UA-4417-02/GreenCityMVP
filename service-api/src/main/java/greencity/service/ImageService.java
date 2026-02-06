package greencity.service;

import greencity.dto.event.EventImageContentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    EventImageContentDto upload(MultipartFile image);
    void delete(String imageUrl);

    default EventImageContentDto getImageContent(UUID id) {
        throw new UnsupportedOperationException("This strategy does not support retrieving image content directly.");
    }
}
