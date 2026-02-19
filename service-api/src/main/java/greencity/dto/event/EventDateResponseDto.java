package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventDateResponseDto(
    Long id,

    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,

    @JsonFormat(pattern = "HH:mm") LocalTime startTime,

    @JsonFormat(pattern = "HH:mm") LocalTime endTime,

    boolean isAllDay,
    AddressDto address,
    String onlineLink) {
}
