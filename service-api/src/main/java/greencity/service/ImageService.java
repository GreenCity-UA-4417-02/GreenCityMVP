package greencity.service;

import greencity.dto.event.EventImageContentDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile image);
    void delete(String imageUrl);

    default EventImageContentDto getImageContent(Long id) {
        throw new UnsupportedOperationException("This strategy does not support retrieving image content directly.");
    }
}
