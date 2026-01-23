package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.annotations.FutureOrPresentDate;
import greencity.annotations.ValidEventTime;
import greencity.annotations.ValidEventTypeConsistency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalTime;

@ValidEventTime
@ValidEventTypeConsistency
public record EventDateDto(
        @NotNull(message = "Event date is required")
        @FutureOrPresentDate(message = "Event date cannot be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        boolean isAllDay,

        @Valid
        AddressDto address,

        @URL(message = "Invalid URL format for online link")
        String onlineLink
) {
    @AssertTrue(message = "End time must be after start time")
    private boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }

    @AssertTrue(message = "For 'All day' events, time should be from 00:00 to 23:59")
    private boolean isAllDayTimeValid() {
        if (!isAllDay) return true;
        return startTime != null && endTime != null;
    }
}
