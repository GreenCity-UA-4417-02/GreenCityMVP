package greencity.dto.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EventImageContentDto(
        UUID id,
        byte[] imageData,
        String contentType,
        String link
) {}