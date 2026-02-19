package greencity.annotations;

import greencity.validator.ValidEventTypeConsistencyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidEventTypeConsistencyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventTypeConsistency {
    String message() default "Event type and location/link consistency error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
