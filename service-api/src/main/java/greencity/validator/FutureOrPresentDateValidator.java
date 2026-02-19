package greencity.validator;

import greencity.annotations.FutureOrPresentDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureOrPresentDateValidator implements ConstraintValidator<FutureOrPresentDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(LocalDate.now());
    }
}
