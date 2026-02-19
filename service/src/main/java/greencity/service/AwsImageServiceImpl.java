package greencity.service;

import greencity.dto.event.EventImageContentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "greencity.images.strategy", havingValue = "aws")
public class AwsImageServiceImpl implements ImageService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public EventImageContentDto upload(MultipartFile image) {
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
            ? originalFileName.substring(originalFileName.lastIndexOf("."))
            : "";
        String fileName = UUID.randomUUID() + extension;

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(image.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(
                image.getInputStream(), image.getSize()));

            String url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build()).toExternalForm();

            return EventImageContentDto.builder()
                .id(null)
                .link(url)
                .contentType(image.getContentType())
                .build();
        } catch (IOException e) {
            log.error("Error reading image: {}", e.getMessage());
            throw new RuntimeException("Failed to read image data", e);
        } catch (S3Exception e) {
            log.error("Error uploading to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    @Override
    public void delete(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            s3Client.deleteObject(deleteReq);
            log.info("Deleted file from S3: {}", fileName);
        } catch (Exception e) {
            log.warn("Failed to delete file from S3: {}", imageUrl, e);
        }
    }
}