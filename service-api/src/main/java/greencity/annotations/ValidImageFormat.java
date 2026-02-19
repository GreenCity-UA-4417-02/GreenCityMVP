package greencity.annotations;

import greencity.validator.ValidImageFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
