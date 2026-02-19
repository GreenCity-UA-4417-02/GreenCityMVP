package greencity.validator;

import greencity.annotations.ValidImageFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidImageFormatValidator implements ConstraintValidator<ValidImageFormat, String> {
    private String[] allowedFormats;

    @Override
    public void initialize(ValidImageFormat constraint) {
        this.allowedFormats = constraint.allowedFormats();
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null)
            return true;

        for (String format : allowedFormats) {
            if (url.toLowerCase().endsWith("." + format)) {
                return true;
            }
        }
        return false;
    }
}