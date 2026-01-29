package greencity.mapping;

import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventDtoMapper extends AbstractConverter<Event, EventDto> {
    @Override
    public EventDto convert(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getOrganizer() != null ? event.getOrganizer().getId() : null,
                event.getOrganizer() != null ? event.getOrganizer().getEmail() : null,
                event.getOrganizer() != null ? event.getOrganizer().getName() : null
        );
    }
}