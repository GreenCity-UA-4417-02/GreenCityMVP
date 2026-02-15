package greencity.service;

import greencity.dto.event.EventImageContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsImageServiceImplTest {
    @Mock
    private S3Client s3Client;

    @InjectMocks
    private AwsImageServiceImpl awsImageService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(awsImageService, "bucketName", bucketName);
    }

    @Test
    void upload_ValidImage_ReturnsUrl() throws MalformedURLException {
        MockMultipartFile file = new MockMultipartFile(
            "image", "test.png", "image/png", "test content".getBytes());

        S3Utilities s3Utilities = mock(S3Utilities.class);
        URL mockUrl = new URL("https://test-bucket.s3.amazonaws.com/unique-id.png");

        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn(mockUrl);

        EventImageContentDto result = awsImageService.upload(file);

        assertNotNull(result);
        assertEquals(mockUrl.toString(), result.link());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void upload_S3Exception_ThrowsRuntimeException() {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "data".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenThrow(S3Exception.builder().message("S3 Error").build());

        assertThrows(RuntimeException.class, () -> awsImageService.upload(file));
    }

    @Test
    void delete_ValidUrl_CallsS3Delete() {
        String url = "https://bucket.s3.com/image-name.jpg";

        awsImageService.delete(url);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void delete_Error_DoesNotThrowException() {
        doThrow(S3Exception.class).when(s3Client).deleteObject(any(DeleteObjectRequest.class));
        assertDoesNotThrow(() -> awsImageService.delete("https://bucket.s3.com/image.jpg"));
    }
}