package greencity.dto.event;

import lombok.Builder;

@Builder
public record EventImageContentDto(
        byte[] imageData,
        String contentType
) {}