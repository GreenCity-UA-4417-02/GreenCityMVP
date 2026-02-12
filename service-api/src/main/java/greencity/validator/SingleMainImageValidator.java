package greencity.validator;

import greencity.annotations.SingleMainImage;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventImageDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SingleMainImageValidator implements ConstraintValidator<SingleMainImage, CreateEventRequestDto> {
    @Override
    public boolean isValid(CreateEventRequestDto request, ConstraintValidatorContext context) {
        if (request.images() == null) return true;

        long mainImageCount = request.images().stream().filter(EventImageDto::isMain).count();
        return mainImageCount <= 1;
    }
}