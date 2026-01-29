package greencity.dto.event;

public record EventDto(
        Long id,
        String title,
        Long organizerId,
        String organizerEmail,
        String organizerName
) {
}
