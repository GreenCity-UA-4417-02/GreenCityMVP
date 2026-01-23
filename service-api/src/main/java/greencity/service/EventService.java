package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;

public interface EventService {
    EventResponseDto createEvent(CreateEventRequestDto requestDto, Long organizerId);
}