package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventImageContentDto;
import greencity.entity.event.EventImageContent;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventImageContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "greencity.images.strategy", havingValue = "database", matchIfMissing = true)
public class BlobImageServiceImpl implements ImageService, ImageContentRetriever {
    private final EventImageContentRepository eventImageContentRepository;
    private static final String BASE_URL = "/events/images/content/";

    @Override
    public EventImageContentDto upload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new NotSavedException("Image is empty and cannot be saved");
        }

        try {
            EventImageContent content = EventImageContent.builder()
                    .imageData(image.getBytes())
                    .contentType(image.getContentType())
                    .build();

            EventImageContent savedContent = eventImageContentRepository.save(content);

            String link = "/events/images/content/" + savedContent.getId();

            return EventImageContentDto.builder()
                    .id(savedContent.getId())
                    .imageData(savedContent.getImageData())
                    .contentType(savedContent.getContentType())
                    .link(link)
                    .build();
        } catch (IOException e) {
            log.error("Failed to save image to database: {}", e.getMessage());
            throw new NotSavedException("Failed to convert image to bytes");
        }
    }

    @Override
    public void delete(String imageUrl) {
        if (imageUrl != null && imageUrl.contains(BASE_URL)) {
            try {
                String idStr = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                UUID id = UUID.fromString(idStr);

                eventImageContentRepository.deleteById(id);
            } catch (Exception e) {
                log.error("Failed to delete image content with ID from URL: {}", imageUrl, e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventImageContentDto getImageContent(UUID id) {
        EventImageContent content = eventImageContentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_IMAGE_CONTENT_NOT_FOUND + id));

        return EventImageContentDto.builder()
                .imageData(content.getImageData())
                .contentType(content.getContentType())
                .build();
    }
}