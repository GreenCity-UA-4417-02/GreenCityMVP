package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.entity.User;
import greencity.entity.event.*;
import greencity.enums.EventType;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.ForbiddenException;
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
import java.util.Objects;
import java.util.Optional;

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
    private final EmailNotificationService emailNotificationService;

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

    @Override
    public void deleteEvent(Long eventId, Long userId) {
        log.info("Deleting event with id: {} by userId: {}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessage.EVENT_NOT_FOUND, eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOrganizer && !isAdmin) {
            log.warn("User {} attempted to delete event {} without permissions", userId, eventId);
            throw new ForbiddenException(ErrorMessage.EVENT_FORBIDDEN);
        }

        eventRepository.delete(event);

        for (EventImages image : event.getAdditionalImages()) {
            imageService.delete(image.getLink());
        }

        log.info("Event with id: {} deleted successfully by user: {}", eventId, userId);
        emailNotificationService.sendDeleteNotification(modelMapper.map(event, EventDto.class));
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long eventId, CreateEventRequestDto requestDto, MultipartFile[] images, Long userId) {
        log.info("Updating event with id: {} by userId: {}", eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessage.EVENT_NOT_FOUND, eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOrganizer && !isAdmin) {
            log.warn("User {} attempted to update event {} without permissions", userId, eventId);
            throw new ForbiddenException(ErrorMessage.EVENT_FORBIDDEN);
        }

        LocalDate earliestDate = event.getDates().stream()
                .map(EventDateLocation::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        if (earliestDate.isBefore(LocalDate.now())) {
            log.warn("Attempt to update past event {}", eventId);
            throw new BadRequestException(ErrorMessage.CANNOT_UPDATE_PAST_EVENT);
        }

        boolean hasChanges = detectChanges(event, requestDto);

        event.setTitle(requestDto.title());
        event.setDescription(requestDto.description());
        event.setType(requestDto.type());
        event.getDates().clear();
        event.getDates().addAll(buildEventDates(requestDto, event));

        InitiativeType initiativeType = initiativeTypeRepository.findById(requestDto.initiativeTypeId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_INITIATIVE_TYPE_NOT_FOUND + requestDto.initiativeTypeId()));
        event.setInitiativeType(initiativeType);

        EventCategory eventCategory = eventCategoryRepository.findById(requestDto.eventCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_CATEGORY_NOT_FOUND + requestDto.eventCategoryId()));
        event.setEventCategory(eventCategory);

        event.setOpen(requestDto.isOpen());

        updateEventImages(images, event);

        Event updatedEvent = eventRepository.save(event);
        log.info("Event with id: {} updated successfully", eventId);

        if (hasChanges) {
            emailNotificationService.sendUpdateNotification(modelMapper.map(updatedEvent, EventDto.class));
        }

        return modelMapper.map(updatedEvent, EventResponseDto.class);
    }

    private boolean detectChanges(Event oldEvent, CreateEventRequestDto newData) {
        return !Objects.equals(oldEvent.getTitle(), newData.title())
                || !Objects.equals(oldEvent.getDescription(), newData.description())
                || !Objects.equals(oldEvent.getType(), newData.type())
                || datesChanged(oldEvent.getDates(), newData.dates())
                || !Objects.equals(Optional.ofNullable(oldEvent.getEventCategory()).map(EventCategory::getId).orElse(null),
                newData.eventCategoryId())
                || !Objects.equals(Optional.ofNullable(oldEvent.getInitiativeType()).map(InitiativeType::getId).orElse(null),
                newData.initiativeTypeId())
                || !Objects.equals(oldEvent.isOpen(), newData.isOpen())
                || imagesChanged(oldEvent.getAdditionalImages(), newData.images());
    }

    private boolean datesChanged(List<EventDateLocation> oldDates, List<EventDateDto> newDates) {
        if (oldDates.size() != newDates.size()) {
            return true;
        }

        for (int i = 0; i < oldDates.size(); i++) {
            if (dateChanged(oldDates.get(i), newDates.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean dateChanged(EventDateLocation oldDate, EventDateDto newDate) {
        return !Objects.equals(oldDate.getDate(), newDate.date())
                || !Objects.equals(oldDate.isAllDay(), newDate.isAllDay())
                || !Objects.equals(oldDate.getStartTime(), newDate.startTime())
                || !Objects.equals(oldDate.getEndTime(), newDate.endTime())
                || !Objects.equals(oldDate.getOnlineLink(), newDate.onlineLink())
                || addressChanged(oldDate.getAddress(), newDate.address());
    }

    private boolean imagesChanged(List<EventImages> oldImages, List<EventImageDto> newImages) {
        if (oldImages.size() != newImages.size()) {
            return true;
        }

        for (int i = 0; i < oldImages.size(); i++) {
            if (!Objects.equals(oldImages.get(i).getLink(), newImages.get(i).url())) {
                return true;
            }
        }

        return false;
    }

    private boolean addressChanged(Address oldAddress, AddressDto newAddress) {
        if (oldAddress == null || newAddress == null) {
            return !Objects.equals(oldAddress, newAddress);
        }
        return !Objects.equals(oldAddress.getLatitude(), newAddress.latitude())
                || !Objects.equals(oldAddress.getLongitude(), newAddress.longitude())
                || !Objects.equals(oldAddress.getStreetEn(), newAddress.streetEn())
                || !Objects.equals(oldAddress.getStreetUk(), newAddress.streetUk())
                || !Objects.equals(oldAddress.getHouseNumber(), newAddress.houseNumber())
                || !Objects.equals(oldAddress.getCityEn(), newAddress.cityEn())
                || !Objects.equals(oldAddress.getCityUk(), newAddress.cityUk())
                || !Objects.equals(oldAddress.getRegionEn(), newAddress.regionEn())
                || !Objects.equals(oldAddress.getRegionUk(), newAddress.regionUk())
                || !Objects.equals(oldAddress.getCountryEn(), newAddress.countryEn())
                || !Objects.equals(oldAddress.getCountryUk(), newAddress.countryUk())
                || !Objects.equals(oldAddress.getFormattedAddressEn(), newAddress.formattedAddressEn())
                || !Objects.equals(oldAddress.getFormattedAddressUk(), newAddress.formattedAddressUk());
    }

    private void updateEventImages(MultipartFile[] file, Event event) {
        for (EventImages oldImage : event.getAdditionalImages()) {
            try {
                imageService.delete(oldImage.getLink());
            } catch (Exception e) {
                log.error("Failed to delete old image: {}", oldImage.getLink(), e);
            }
        }
        event.getAdditionalImages().clear();
        event.setTitleImage(null);

        event.getAdditionalImages().addAll(processImages(file, event));
    }
}