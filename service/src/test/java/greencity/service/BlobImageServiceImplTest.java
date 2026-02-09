package greencity.service;

import greencity.dto.event.EventImageContentDto;
import greencity.entity.event.EventImageContent;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventImageContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlobImageServiceImplTest {
    @Mock
    private EventImageContentRepository eventImageContentRepository;

    @InjectMocks
    private BlobImageServiceImpl blobImageService;

    @Test
    void upload_ValidImage_ReturnsDto() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test data".getBytes());
        UUID id = UUID.randomUUID();
        EventImageContent content = EventImageContent.builder()
                .id(id)
                .imageData(file.getBytes())
                .contentType(file.getContentType())
                .build();

        when(eventImageContentRepository.save(any(EventImageContent.class))).thenReturn(content);

        EventImageContentDto result = blobImageService.upload(file);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertTrue(result.link().contains(id.toString()));
        verify(eventImageContentRepository, times(1)).save(any());
    }

    @Test
    void upload_EmptyImage_ThrowsNotSavedException() {
        MockMultipartFile file = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        assertThrows(NotSavedException.class, () -> blobImageService.upload(file));
    }

    @Test
    void delete_ValidUrl_CallsRepository() {
        UUID id = UUID.randomUUID();
        String url = "/events/images/content/" + id;

        blobImageService.delete(url);

        verify(eventImageContentRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_InvalidUrl_DoesNothing() {
        blobImageService.delete("invalid/url/path");
        verify(eventImageContentRepository, never()).deleteById(any());
    }

    @Test
    void getImageContent_ExistingId_ReturnsDto() {
        UUID id = UUID.randomUUID();
        byte[] data = "data".getBytes();
        EventImageContent content = EventImageContent.builder()
                .imageData(data)
                .contentType("image/png")
                .build();

        when(eventImageContentRepository.findById(id)).thenReturn(Optional.of(content));

        EventImageContentDto result = blobImageService.getImageContent(id);

        assertArrayEquals(data, result.imageData());
        assertEquals("image/png", result.contentType());
    }

    @Test
    void getImageContent_NonExistingId_ThrowsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(eventImageContentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> blobImageService.getImageContent(id));
    }
}