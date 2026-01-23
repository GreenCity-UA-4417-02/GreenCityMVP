package greencity.annotations;

import greencity.validator.EventTypeConsistencyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventTypeConsistencyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTypeConsistency {
    String message() default "Event type and location/link consistency error";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
