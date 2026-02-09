package greencity.annotations;

import greencity.validator.ValidImageFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageFormat {
    String message() default "Invalid image format";
    String[] allowedFormats() default {"jpg", "jpeg", "png"};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

