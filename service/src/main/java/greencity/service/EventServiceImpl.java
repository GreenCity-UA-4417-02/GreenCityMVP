package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.User;
import greencity.entity.event.*;
import greencity.enums.EventType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCategoryRepository;
import greencity.repository.EventRepository;
import greencity.repository.InitiativeTypeRepository;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepo userRepository;
    private final InitiativeTypeRepository initiativeTypeRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public EventResponseDto createEvent(CreateEventRequestDto requestDto, MultipartFile[] images, Long organizerId) {
        log.info("Creating event with title: '{}' by organizerId: {}", requestDto.title(), organizerId);

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_ORGANIZER_NOT_FOUND + organizerId));

        InitiativeType initiativeType = initiativeTypeRepository.findById(requestDto.initiativeTypeId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_INITIATIVE_TYPE_NOT_FOUND + requestDto.initiativeTypeId()));

        EventCategory eventCategory = eventCategoryRepository.findById(requestDto.eventCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_CATEGORY_NOT_FOUND + requestDto.eventCategoryId()));

        Event event = modelMapper.map(requestDto, Event.class);
        event.setOrganizer(organizer);
        event.setCreationDate(LocalDate.now());
        event.setInitiativeType(initiativeType);
        event.setEventCategory(eventCategory);
        event.setType(EventType.valueOf(requestDto.type().name()));

        event.setDates(buildEventDates(requestDto, event));
        event.setAdditionalImages(buildEventImages(requestDto, event));

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with id: {}", savedEvent.getId());

        return modelMapper.map(savedEvent, EventResponseDto.class);
    }

    private List<EventDateLocation> buildEventDates(CreateEventRequestDto requestDto, Event event) {
        List<EventDateLocation> dates = new ArrayList<>();

        for (greencity.dto.event.EventDateDto dateDto : requestDto.dates()) {
            EventDateLocation eventDate = EventDateLocation.builder()
                    .date(dateDto.date())
                    .startTime(dateDto.startTime())
                    .endTime(dateDto.endTime())
                    .isAllDay(dateDto.isAllDay())
                    .event(event)
                    .build();

            if (dateDto.address() != null) {
                Address address = Address.builder()
                        .latitude(dateDto.address().latitude())
                        .longitude(dateDto.address().longitude())
                        .streetUk(dateDto.address().streetUk())
                        .cityUk(dateDto.address().cityUk())
                        .countryUk(dateDto.address().countryUk())
                        .formattedAddressUk(dateDto.address().formattedAddressUk())
                        .build();
                eventDate.setAddress(address);
            }

            if (dateDto.onlineLink() != null) {
                eventDate.setOnlineLink(dateDto.onlineLink());
            }

            dates.add(eventDate);
        }

        return dates;
    }

    private List<EventImages> buildEventImages(CreateEventRequestDto requestDto, Event event) {
        if (requestDto.images() == null || requestDto.images().isEmpty()) {
            return new ArrayList<>();
        }

        List<EventImages> images = new ArrayList<>();
        boolean hasMainImage = false;

        for (greencity.dto.event.EventImageDto imageDto : requestDto.images()) {
            EventImages eventImage = EventImages.builder()
                    .link(imageDto.url())
                    .event(event)
                    .isMain(imageDto.isMain())
                    .build();

            images.add(eventImage);

            if (imageDto.isMain()) {
                hasMainImage = true;
                event.setTitleImage(imageDto.url());
            }
        }

        if (!hasMainImage && !images.isEmpty()) {
            EventImages firstImage = images.getFirst();
            firstImage.setMain(true);
            event.setTitleImage(firstImage.getLink());
        }

        return images;
    }
}