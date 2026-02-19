package greencity.validator;

import greencity.annotations.EventTypeConsistency;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventTypeConsistencyValidator implements ConstraintValidator<EventTypeConsistency, CreateEventRequestDto> {
    @Override
    public boolean isValid(CreateEventRequestDto request, ConstraintValidatorContext context) {
        if (request == null || request.dates() == null || request.type() == null) {
            return true;
        }

        for (EventDateDto date : request.dates()) {
            boolean hasAddress = date.address() != null;
            boolean hasOnlineLink = date.onlineLink() != null && !date.onlineLink().isBlank();

            switch (request.type()) {
                case OFFLINE:
                    if (!hasAddress || hasOnlineLink) {
                        addConstraintViolation(context,
                            "OFFLINE events require address and should not have online link");
                        return false;
                    }
                    break;
                case ONLINE:
                    if (!hasOnlineLink || hasAddress) {
                        addConstraintViolation(context,
                            "ONLINE events require online link and should not have address");
                        return false;
                    }
                    break;
                case ONLINE_OFFLINE:
                    if (!hasAddress || !hasOnlineLink) {
                        addConstraintViolation(context,
                            "ONLINE_OFFLINE events require both address and online link");
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}