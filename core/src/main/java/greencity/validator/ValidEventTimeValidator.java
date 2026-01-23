package greencity.validator;

import greencity.annotations.ValidEventTime;
import greencity.dto.event.EventDateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalTime;

public class ValidEventTimeValidator implements ConstraintValidator<ValidEventTime, EventDateDto> {
    @Override
    public boolean isValid(EventDateDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        if (LocalDate.now().equals(dto.date()) && dto.startTime() != null) {
            return !dto.startTime().isBefore(LocalTime.now());
        }

        if (dto.isAllDay()) {
            return LocalTime.of(0, 0).equals(dto.startTime()) &&
                   LocalTime.of(23, 59).equals(dto.endTime());
        }

        return true;
    }
}