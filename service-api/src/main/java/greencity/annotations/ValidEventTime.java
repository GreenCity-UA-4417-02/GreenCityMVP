package greencity.annotations;

import greencity.validator.ValidEventTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEventTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventTime {
    String message() default "Invalid event time configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
