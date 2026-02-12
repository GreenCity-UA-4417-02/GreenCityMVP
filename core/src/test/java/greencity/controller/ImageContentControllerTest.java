package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.event.EventImageContentDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.ImageContentRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageContentControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ImageContentRetriever imageService;

    @Mock
    private ErrorAttributes errorAttributes;

    @InjectMocks
    private ImageContentController imageContentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        when(errorAttributes.getErrorAttributes(any(WebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(new HashMap<>(Map.of(
                        "message", "error",
                        "timestamp", "2026-02-08T14:04:12Z",
                        "path", "/events",
                        "status", 404
                )));

        this.mockMvc = MockMvcBuilders.standaloneSetup(imageContentController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void getImage_Success() throws Exception {
        UUID id = UUID.randomUUID();
        EventImageContentDto dto = ModelUtils.getEventImageContentDto();

        when(imageService.getImageContent(id)).thenReturn(dto);

        mockMvc.perform(get("/events/images/content/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(dto.imageData()));

        verify(imageService).getImageContent(id);
    }

    @Test
    void getImage_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(imageService.getImageContent(id)).thenThrow(new NotFoundException("Image not found"));

        mockMvc.perform(get("/events/images/content/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getImage_InvalidUuidFormat() throws Exception {
        mockMvc.perform(get("/events/images/content/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}