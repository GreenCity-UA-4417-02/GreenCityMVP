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
        if (dto == null)
            return true;

        if (LocalDate.now().equals(dto.date())) {
            if (dto.startTime() != null && dto.startTime().isBefore(LocalTime.now())) {
                context.buildConstraintViolationWithTemplate(
                    "Cannot set past time for current date")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}