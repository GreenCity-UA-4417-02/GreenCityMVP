package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.event.*;
import greencity.entity.User;
import greencity.entity.event.*;
import greencity.enums.EventType;
import greencity.enums.Role;
import greencity.exception.exceptions.ForbiddenException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepo userRepository;

    @Mock
    private InitiativeTypeRepository initiativeTypeRepository;

    @Mock
    private EventCategoryRepository eventCategoryRepository;

    @Mock
    private EventImageContentRepository eventImageContentRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createEvent_ValidRequest_Success() {
        Long organizerId = 1L;
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        MultipartFile[] images = new MultipartFile[]{
                new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes())
        };

        User organizer = User.builder().id(organizerId).name("Name").build();
        InitiativeType initiativeType = new InitiativeType(1L, "Social", "Desc");
        EventCategory category = new EventCategory(1L, "Category", "Desc");
        Event event = new Event();

        EventImageContentDto imageDto = EventImageContentDto.builder()
                .id(UUID.randomUUID())
                .link("http://image.link")
                .build();

        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(initiativeTypeRepository.findById(requestDto.initiativeTypeId())).thenReturn(Optional.of(initiativeType));
        when(eventCategoryRepository.findById(requestDto.eventCategoryId())).thenReturn(Optional.of(category));

        when(modelMapper.map(requestDto, Event.class)).thenReturn(event);
        when(imageService.upload(any())).thenReturn(imageDto);
        when(eventImageContentRepository.getReferenceById(imageDto.id())).thenReturn(new EventImageContent());

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(ModelUtils.getEventResponseDto());

        EventResponseDto result = eventService.createEvent(requestDto, images, organizerId);

        assertNotNull(result);
        verify(userRepository).findById(organizerId);
        verify(eventRepository).save(any(Event.class));
        verify(imageService, times(1)).upload(any());
        assertEquals("http://image.link", event.getTitleImage());
    }

    @Test
    void createEvent_OrganizerNotFound_ThrowsNotFoundException() {
        Long organizerId = 1L;
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        when(userRepository.findById(organizerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> eventService.createEvent(requestDto, null, organizerId));

        assertTrue(exception.getMessage().contains(ErrorMessage.EVENT_ORGANIZER_NOT_FOUND));
    }

    @Test
    void createEvent_InitiativeTypeNotFound_ThrowsNotFoundException() {
        Long organizerId = 1L;
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();

        when(userRepository.findById(organizerId)).thenReturn(Optional.of(new User()));
        when(initiativeTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> eventService.createEvent(requestDto, null, organizerId));
    }

    @Test
    void createEvent_WithMultipleImages_SetsFirstAsMain() {
        Long organizerId = 1L;
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        MultipartFile[] images = new MultipartFile[]{
                new MockMultipartFile("img1", "1.jpg", "image/jpeg", "d1".getBytes()),
                new MockMultipartFile("img2", "2.jpg", "image/jpeg", "d2".getBytes())
        };

        Event event = new Event();
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(initiativeTypeRepository.findById(any())).thenReturn(Optional.of(new InitiativeType()));
        when(eventCategoryRepository.findById(any())).thenReturn(Optional.of(new EventCategory()));
        when(modelMapper.map(any(CreateEventRequestDto.class), eq(Event.class))).thenReturn(event);
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(responseDto);

        EventImageContentDto imgDto1 = EventImageContentDto.builder().link("link1").id(UUID.randomUUID()).build();
        EventImageContentDto imgDto2 = EventImageContentDto.builder().link("link2").id(UUID.randomUUID()).build();

        when(imageService.upload(images[0])).thenReturn(imgDto1);
        when(imageService.upload(images[1])).thenReturn(imgDto2);

        when(eventImageContentRepository.getReferenceById(any())).thenReturn(new EventImageContent());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.createEvent(requestDto, images, organizerId);

        assertEquals("link1", event.getTitleImage());
        assertNotNull(event.getAdditionalImages());
        assertEquals(2, event.getAdditionalImages().size());

        assertTrue(event.getAdditionalImages().get(0).isMain(), "First image should be main");
        assertFalse(event.getAdditionalImages().get(1).isMain(), "Second image should NOT be main");

        verify(imageService, times(2)).upload(any(MultipartFile.class));
    }

    @Test
    void createEvent_NoImages_Success() {
        Long organizerId = 1L;
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        Event event = new Event();
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(initiativeTypeRepository.findById(any())).thenReturn(Optional.of(new InitiativeType()));
        when(eventCategoryRepository.findById(any())).thenReturn(Optional.of(new EventCategory()));

        when(modelMapper.map(any(CreateEventRequestDto.class), eq(Event.class))).thenReturn(event);
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(responseDto);

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponseDto result = eventService.createEvent(requestDto, null, organizerId);

        assertNotNull(result);
        assertEquals(responseDto.title(), result.title());
        verify(imageService, never()).upload(any());
    }

    @Test
    void deleteEventTest() {
        Long eventId = 1L;
        Long userId = 2L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("title");
        User user = new User();
        user.setId(userId);
        user.setRole(Role.ROLE_USER);
        user.setName("Organiser");
        user.setEmail("example@gmail.com");
        event.setOrganizer(user);

        List<EventImages> images = new ArrayList<>();

        EventImages image = new EventImages();
        image.setId(1L);
        image.setLink("https://example.com/image.jpg");
        image.setEvent(event);
        images.add(image);

        event.setAdditionalImages(images);

        EventDto eventDto = new EventDto(eventId, event.getTitle(), userId, user.getEmail(), user.getName());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(Event.class), eq(EventDto.class))).thenReturn(eventDto);

        eventService.deleteEvent(eventId, userId);

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verify(eventRepository).delete(event);
        verify(imageService).delete("https://example.com/image.jpg");
        verify(modelMapper).map(event, EventDto.class);
        verify(emailNotificationService).sendDeleteNotification(eventDto);
    }

    @Test
    void deleteEventAsAdminTest() {
        Long eventId = 1L;
        Long userId = 2L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("title");
        User user = new User();
        user.setId(userId);
        user.setRole(Role.ROLE_ADMIN);

        User organizer = new User();
        organizer.setId(3L);
        organizer.setName("Organiser");
        organizer.setEmail("example@gmail.com");
        event.setOrganizer(organizer);

        List<EventImages> images = new ArrayList<>();

        EventImages image = new EventImages();
        image.setId(1L);
        image.setLink("https://example.com/image.jpg");
        image.setEvent(event);
        images.add(image);

        event.setAdditionalImages(images);

        EventDto eventDto = new EventDto(eventId, event.getTitle(), organizer.getId(), organizer.getEmail(), organizer.getName());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(Event.class), eq(EventDto.class))).thenReturn(eventDto);

        eventService.deleteEvent(eventId, userId);

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verify(eventRepository).delete(event);
        verify(imageService).delete("https://example.com/image.jpg");
        verify(modelMapper).map(event, EventDto.class);
        verify(emailNotificationService).sendDeleteNotification(eventDto);
    }

    @Test
    void deleteEventAsNeitherAdminNorOrganizerTest() {
        Long eventId = 1L;
        Long userId = 2L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User();
        user.setId(userId);
        user.setRole(Role.ROLE_USER);
        event.setOrganizer(user);

        User organizer = new User();
        organizer.setId(3L);
        event.setOrganizer(organizer);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("Only event organizer or admin can manage event", exception.getMessage());
        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(imageService);
        verifyNoInteractions(emailNotificationService);
    }

    @Test
    void deleteEventEventNotFoundTest() {
        Long eventId = 1L;
        Long userId = 2L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("Event with id 1 not found", exception.getMessage());
        verify(eventRepository).findById(eventId);
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(imageService);
        verifyNoInteractions(emailNotificationService);
    }

    @Test
    void deleteEventUserNotFoundTest() {
        Long eventId = 1L;
        Long userId = 2L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("The user does not exist by this id: 2", exception.getMessage());
        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(imageService);
        verifyNoInteractions(emailNotificationService);
    }

    @Test
    void updateEventTest() {
        Long eventId = 1L;
        Long userId = 2L;

        User organizer = new User();
        organizer.setId(userId);
        organizer.setRole(Role.ROLE_USER);
        organizer.setName("Organizer");
        organizer.setEmail("organizer@example.com");

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Title");
        event.setDescription("Description");
        event.setType(EventType.OFFLINE);
        event.setOrganizer(organizer);
        EventDateLocation date = new EventDateLocation();
        date.setDate(LocalDate.now());
        event.setDates(new ArrayList<>(List.of(date)));
        event.setCreationDate(LocalDate.now());
        event.setOpen(false);

        EventDateDto dateDto = new EventDateDto(
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                LocalTime.now().plusHours(1),
                true,
                null,
                "http://localhost"
        );

        CreateEventRequestDto requestDto = new CreateEventRequestDto(
                "Updated Title",
                List.of(dateDto),
                "Updated description",
                EventType.ONLINE,
                List.of(),
                1L,
                1L,
                true
        );

        InitiativeType initiativeType = new InitiativeType();
        initiativeType.setId(1L);

        EventCategory eventCategory = new EventCategory();
        eventCategory.setId(1L);

        Event updatedEvent = new Event();
        updatedEvent.setId(eventId);
        updatedEvent.setTitle(requestDto.title());
        updatedEvent.setDescription(requestDto.description());
        updatedEvent.setType(requestDto.type());
        updatedEvent.setOrganizer(organizer);
        EventDateLocation updatedDate = new EventDateLocation();
        updatedDate.setId(1L);
        updatedDate.setDate(dateDto.date());
        updatedDate.setStartTime(dateDto.startTime());
        updatedDate.setEndTime(dateDto.endTime());
        updatedDate.setAllDay(dateDto.isAllDay());
        updatedDate.setOnlineLink(dateDto.onlineLink());
        updatedEvent.setDates(List.of(updatedDate));

        EventDto eventDto = new EventDto(eventId, requestDto.title(), userId,
                organizer.getEmail(), organizer.getName());

        EventResponseDto expectedResponse = new EventResponseDto(
                eventId,
                requestDto.title(),
                null,
                userId,
                organizer.getName(),
                event.getCreationDate(),
                requestDto.description(),
                requestDto.isOpen(),
                requestDto.type(),
                List.of(new EventDateResponseDto(updatedDate.getId(), dateDto.date(), dateDto.startTime(),
                        dateDto.endTime(), dateDto.isAllDay(), dateDto.address(), dateDto.onlineLink())
                ),
                List.of(),
                new InitiativeTypeDto(initiativeType.getId(), initiativeType.getName(), initiativeType.getDescription()),
                new EventCategoryDto(eventCategory.getId(), eventCategory.getName(), eventCategory.getDescription()),
                0,
                0,
                0,
                0,
                event.getCreationDate()
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(organizer));
        when(initiativeTypeRepository.findById(1L)).thenReturn(Optional.of(initiativeType));
        when(eventCategoryRepository.findById(1L)).thenReturn(Optional.of(eventCategory));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(any(Event.class), eq(EventDto.class))).thenReturn(eventDto);
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(expectedResponse);

        MultipartFile[] images = new MultipartFile[0];

        EventResponseDto result = eventService.updateEvent(eventId, requestDto, images, userId);

        assertEquals(expectedResponse, result);

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verify(initiativeTypeRepository).findById(1L);
        verify(eventCategoryRepository).findById(1L);
        verify(eventRepository).save(event);
        verify(modelMapper).map(updatedEvent, EventDto.class);
        verify(emailNotificationService).sendUpdateNotification(eventDto);
        verify(modelMapper).map(updatedEvent, EventResponseDto.class);
    }

    @Test
    void updateEventAsAdminTest() {
        Long eventId = 1L;
        Long userId = 2L;

        User admin = new User();
        admin.setId(userId);
        admin.setRole(Role.ROLE_ADMIN);
        admin.setName("Admin");
        admin.setEmail("admin@example.com");

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Title");
        event.setDescription("Description");
        event.setType(EventType.OFFLINE);
        EventDateLocation date = new EventDateLocation();
        date.setDate(LocalDate.now());
        event.setDates(new ArrayList<>(List.of(date)));
        event.setCreationDate(LocalDate.now());
        event.setOpen(false);

        EventDateDto dateDto = new EventDateDto(
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                LocalTime.now().plusHours(1),
                true,
                null,
                "http://localhost"
        );

        CreateEventRequestDto requestDto = new CreateEventRequestDto(
                "Updated Title",
                List.of(dateDto),
                "Updated description",
                EventType.ONLINE,
                List.of(),
                1L,
                1L,
                true
        );

        InitiativeType initiativeType = new InitiativeType();
        initiativeType.setId(1L);

        EventCategory eventCategory = new EventCategory();
        eventCategory.setId(1L);

        Event updatedEvent = new Event();
        updatedEvent.setId(eventId);
        updatedEvent.setTitle(requestDto.title());
        updatedEvent.setDescription(requestDto.description());
        updatedEvent.setType(requestDto.type());
        EventDateLocation updatedDate = new EventDateLocation();
        updatedDate.setId(1L);
        updatedDate.setDate(dateDto.date());
        updatedDate.setStartTime(dateDto.startTime());
        updatedDate.setEndTime(dateDto.endTime());
        updatedDate.setAllDay(dateDto.isAllDay());
        updatedDate.setOnlineLink(dateDto.onlineLink());
        updatedEvent.setDates(List.of(updatedDate));

        EventDto eventDto = new EventDto(eventId, requestDto.title(), userId,
                admin.getEmail(), admin.getName());

        EventResponseDto expectedResponse = new EventResponseDto(
                eventId,
                requestDto.title(),
                null,
                userId,
                admin.getName(),
                event.getCreationDate(),
                requestDto.description(),
                requestDto.isOpen(),
                requestDto.type(),
                List.of(new EventDateResponseDto(updatedDate.getId(), dateDto.date(), dateDto.startTime(),
                        dateDto.endTime(), dateDto.isAllDay(), dateDto.address(), dateDto.onlineLink())
                ),
                List.of(),
                new InitiativeTypeDto(initiativeType.getId(), initiativeType.getName(), initiativeType.getDescription()),
                new EventCategoryDto(eventCategory.getId(), eventCategory.getName(), eventCategory.getDescription()),
                0,
                0,
                0,
                0,
                event.getCreationDate()
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(initiativeTypeRepository.findById(1L)).thenReturn(Optional.of(initiativeType));
        when(eventCategoryRepository.findById(1L)).thenReturn(Optional.of(eventCategory));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(any(Event.class), eq(EventDto.class))).thenReturn(eventDto);
        when(modelMapper.map(any(Event.class), eq(EventResponseDto.class))).thenReturn(expectedResponse);

        MultipartFile[] images = new MultipartFile[0];

        EventResponseDto result = eventService.updateEvent(eventId, requestDto, images, userId);

        assertEquals(expectedResponse, result);

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verify(initiativeTypeRepository).findById(1L);
        verify(eventCategoryRepository).findById(1L);
        verify(eventRepository).save(event);
        verify(modelMapper).map(updatedEvent, EventDto.class);
        verify(emailNotificationService).sendUpdateNotification(eventDto);
        verify(modelMapper).map(updatedEvent, EventResponseDto.class);
    }

    @Test
    void updateEventAsNeitherAdminNorOrganizerTest() {
        Long eventId = 1L;
        Long userId = 2L;
        Long organizerId = 3L;

        User user = new User();
        user.setId(userId);
        user.setRole(Role.ROLE_USER);


        User organizer = new User();
        organizer.setId(organizerId);
        organizer.setRole(Role.ROLE_USER);
        organizer.setName("Organizer");
        organizer.setEmail("organizer@example.com");

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Title");
        event.setDescription("Description");
        event.setType(EventType.OFFLINE);
        event.setOrganizer(organizer);
        EventDateLocation date = new EventDateLocation();
        date.setDate(LocalDate.now());
        event.setDates(new ArrayList<>(List.of(date)));
        event.setCreationDate(LocalDate.now());
        event.setOpen(false);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(organizer));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("Only event organizer or admin can manage event", exception.getMessage());

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verifyNoInteractions(initiativeTypeRepository);
        verifyNoInteractions(eventCategoryRepository);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(emailNotificationService);
    }

    @Test
    void updateEventEventNotFoundTest() {
        Long eventId = 1L;
        Long userId = 2L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("Event with id 1 not found", exception.getMessage());

        verify(eventRepository).findById(eventId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(initiativeTypeRepository);
        verifyNoInteractions(eventCategoryRepository);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(emailNotificationService);
    }
    @Test
    void updateEventUserNotFoundTest() {
        Long eventId = 1L;
        Long userId = 2L;

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Title");
        event.setDescription("Description");
        event.setType(EventType.OFFLINE);
        EventDateLocation date = new EventDateLocation();
        date.setDate(LocalDate.now());
        event.setDates(new ArrayList<>(List.of(date)));
        event.setCreationDate(LocalDate.now());
        event.setOpen(false);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId, userId));

        assertEquals("The user does not exist by this id: 2", exception.getMessage());

        verify(eventRepository).findById(eventId);
        verify(userRepository).findById(userId);
        verifyNoInteractions(initiativeTypeRepository);
        verifyNoInteractions(eventCategoryRepository);
        verifyNoMoreInteractions(eventRepository);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(emailNotificationService);
    }
}