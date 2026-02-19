package greencity.controller;

import greencity.dto.event.EventImageContentDto;
import greencity.service.ImageContentRetriever;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events/images/content")
@RequiredArgsConstructor
@Tag(name = "image-content-controller")
@ConditionalOnBean(ImageContentRetriever.class)
public class ImageContentController {
    private final ImageContentRetriever imageService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Get event image content",
        description = "Returns image binary content by image id stored in database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image content successfully returned"),
        @ApiResponse(responseCode = "400", description = "Invalid image id"),
        @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<byte[]> getImage(
        @Parameter(description = "Image id", required = true,
            example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
        EventImageContentDto imageDto = imageService.getImageContent(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(imageDto.contentType()))
            .body(imageDto.imageData());
    }
}
