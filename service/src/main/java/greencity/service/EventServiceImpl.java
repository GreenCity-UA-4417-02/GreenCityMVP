package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateDto;
import greencity.dto.event.EventImageContentDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.User;
import greencity.entity.event.*;
import greencity.enums.EventType;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.*;
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
    private final EventImageContentRepository eventImageContentRepository;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

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

        if (images != null && images.length > 0) {
            List<EventImages> eventImages = processImages(images, event);
            event.setAdditionalImages(eventImages);
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with id: {}", savedEvent.getId());

        return modelMapper.map(savedEvent, EventResponseDto.class);
    }

    private List<EventImages> processImages(MultipartFile[] files, Event event) {
        List<EventImages> eventImages = new ArrayList<>();
        if (files == null || files.length == 0) return eventImages;

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file == null || file.isEmpty()) continue;

            EventImageContentDto dto = imageService.upload(file);
            boolean isMain = (i == 0);

            EventImages imageEntity = EventImages.builder()
                    .link(dto.link())
                    .event(event)
                    .isMain(isMain)
                    .build();

            if (dto.id() != null) {
                EventImageContent content = eventImageContentRepository.getReferenceById(dto.id());
                imageEntity.setContent(content);
            }

            if (isMain) {
                event.setTitleImage(dto.link());
            }

            eventImages.add(imageEntity);
        }

        return eventImages;
    }

    private List<EventDateLocation> buildEventDates(CreateEventRequestDto requestDto, Event event) {
        List<EventDateLocation> dates = new ArrayList<>();

        for (EventDateDto dateDto : requestDto.dates()) {
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
}