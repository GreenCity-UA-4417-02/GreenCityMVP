package greencity.dto.event;

import lombok.Builder;

@Builder
public record EventImageContentDto(
        Long id,
        byte[] imageData,
        String contentType,
        String link
) {}