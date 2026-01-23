package greencity.annotations;

import greencity.validator.SingleMainImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SingleMainImageValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleMainImage {
    String message() default "Only one image can be marked as main";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}