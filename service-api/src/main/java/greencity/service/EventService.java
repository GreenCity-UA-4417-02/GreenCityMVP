package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponseDto createEvent(CreateEventRequestDto requestDto, MultipartFile[] images, Long organizerId);
}