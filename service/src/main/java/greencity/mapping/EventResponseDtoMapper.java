package greencity.mapping;

import greencity.dto.event.*;
import greencity.entity.event.*;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EventResponseDtoMapper extends AbstractConverter<Event, EventResponseDto> {
    @Override
    public EventResponseDto convert(Event event) {
        return new EventResponseDto(
            event.getId(),
            event.getTitle(),
            event.getTitleImage(),
            event.getOrganizer() != null ? event.getOrganizer().getId() : null,
            event.getOrganizer() != null ? event.getOrganizer().getName() : null,
            event.getCreationDate(),
            event.getDescription(),
            event.isOpen(),
            event.getType(),
            mapEventDates(event.getDates()),
            mapEventImages(event.getAdditionalImages()),
            mapInitiativeType(event.getInitiativeType()),
            mapEventCategory(event.getEventCategory()),
            0,
            0,
            0,
            0,
            event.getCreationDate());
    }

    private List<EventDateResponseDto> mapEventDates(List<EventDateLocation> dates) {
        if (dates == null)
            return List.of();
        return dates.stream().map(this::mapEventDate).toList();
    }

    private EventDateResponseDto mapEventDate(EventDateLocation date) {
        return new EventDateResponseDto(
            date.getId(),
            date.getDate(),
            date.getStartTime(),
            date.getEndTime(),
            date.isAllDay(),
            mapAddress(date.getAddress()),
            date.getOnlineLink());
    }

    private AddressDto mapAddress(Address address) {
        if (address == null)
            return null;

        return new AddressDto(
            address.getLatitude(),
            address.getLongitude(),
            address.getStreetUk(),
            address.getStreetEn(),
            address.getHouseNumber(),
            address.getCityUk(),
            address.getCityEn(),
            address.getRegionUk(),
            address.getRegionEn(),
            address.getCountryUk(),
            address.getCountryEn(),
            address.getFormattedAddressUk(),
            address.getFormattedAddressEn());
    }

    private List<EventImageResponseDto> mapEventImages(List<EventImages> images) {
        if (images == null)
            return List.of();
        return images.stream().map(this::mapEventImage).toList();
    }

    private EventImageResponseDto mapEventImage(EventImages image) {
        return new EventImageResponseDto(
            image.getId(),
            image.getLink(),
            image.isMain());
    }

    private InitiativeTypeDto mapInitiativeType(InitiativeType initiativeType) {
        if (initiativeType == null)
            return null;

        return new InitiativeTypeDto(
            initiativeType.getId(),
            initiativeType.getName(),
            initiativeType.getDescription());
    }

    private EventCategoryDto mapEventCategory(EventCategory eventCategory) {
        if (eventCategory == null)
            return null;

        return new EventCategoryDto(
            eventCategory.getId(),
            eventCategory.getName(),
            eventCategory.getDescription());
    }
}