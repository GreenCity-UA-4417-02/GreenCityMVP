package greencity.service;

import greencity.dto.event.EventImageContentDto;
import greencity.repository.EventImageContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "greencity.images.strategy", havingValue = "aws", matchIfMissing = true)
public class AwsImageServiceImpl implements ImageService {
    private final EventImageContentRepository eventImageContentRepository;

    @Override
    public EventImageContentDto upload(MultipartFile image) {
        return null;
    }

    @Override
    public void delete(String imageUrl) {

    }

    @Override
    public EventImageContentDto getImageContent(UUID id) {
        return ImageService.super.getImageContent(id);
    }
}
