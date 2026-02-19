package greencity.service;

import greencity.dto.event.EventImageContentDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    EventImageContentDto upload(MultipartFile image);

    void delete(String imageUrl);
}
