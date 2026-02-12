package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.enums.EventType;

import java.time.LocalDate;
import java.util.List;

public record EventResponseDto(
        Long id,
        String title,
        String titleImage,
        Long organizerId,
        String organizerName,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate creationDate,

        String description,
        boolean isOpen,
        EventType type,
        List<EventDateResponseDto> dates,
        List<EventImageResponseDto> images,
        InitiativeTypeDto initiativeType,
        EventCategoryDto eventCategory,
        Integer attendersCount,
        Integer followersCount,
        Integer likesCount,
        Integer dislikesCount,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate createdAt
) {}
