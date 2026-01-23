package greencity.dto.event;

import greencity.annotations.EventTypeConsistency;
import greencity.annotations.SingleMainImage;
import greencity.enums.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@SingleMainImage
@EventTypeConsistency
public record CreateEventRequestDto(
        @NotBlank(message = "Event title is required")
        @Size(max = 70, message = "Event title cannot exceed 70 characters")
        String title,

        @NotNull(message = "At least one date is required")
        @Size(min = 1, max = 7, message = "You can add from 1 to 7 dates")
        List<@Valid EventDateDto> dates,

        @NotBlank(message = "Event description is required")
        @Size(min = 20, max = 63206, message = "Description must be between 20 and 63,206 characters")
        String description,

        @NotNull(message = "Event type is required")
        EventType type,

        @Size(max = 5, message = "Cannot upload more than 5 images")
        List<@Valid EventImageDto> images,

        @NotNull(message = "Initiative type is required")
        Long initiativeTypeId,

        @NotNull(message = "Event category is required")
        Long eventCategoryId,

        boolean isOpen
) {
    public CreateEventRequestDto {
        if (dates == null) dates = List.of();
        if (images == null) images = List.of();
        isOpen = true;
    }
}
