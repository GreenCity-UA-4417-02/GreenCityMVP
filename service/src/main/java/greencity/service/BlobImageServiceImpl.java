package greencity.service;

import greencity.exception.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlobImageServiceImpl implements ImageService {
    @Override
    public String upload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image file is empty");
        }

        return UUID.randomUUID() + "_" + image.getOriginalFilename();
    }

    @Override
    public void delete(String imageUrl) {

    }
}