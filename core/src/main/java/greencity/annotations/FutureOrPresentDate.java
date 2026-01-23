package greencity.annotations;

import greencity.validator.FutureOrPresentDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureOrPresentDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentDate {
    String message() default "Date cannot be in the past";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
