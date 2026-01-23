package greencity.validator;

import greencity.annotations.ValidEventTypeConsistency;
import greencity.dto.event.EventDateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEventTypeConsistencyValidator implements ConstraintValidator<ValidEventTypeConsistency, EventDateDto> {
    @Override
    public boolean isValid(EventDateDto dto, ConstraintValidatorContext context) {
        return true;
    }
}