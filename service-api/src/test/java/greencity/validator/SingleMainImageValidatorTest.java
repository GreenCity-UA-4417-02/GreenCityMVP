package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventImageDto;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleMainImageValidatorTest {
    private final SingleMainImageValidator validator = new SingleMainImageValidator();

    @Test
    void isValid_MultipleMainImages_ReturnsFalse() {
        List<EventImageDto> images = List.of(
            new EventImageDto("http://img1.jpg", true),
            new EventImageDto("http://img2.jpg", true));
        CreateEventRequestDto request = new CreateEventRequestDto("T", List.of(), "D", null, images, 1L, 1L, true);

        assertFalse(validator.isValid(request, null));
    }

    @Test
    void isValid_SingleMainImage_ReturnsTrue() {
        CreateEventRequestDto request = ModelUtils.getCreateEventRequestDto();
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValid_NoImages_ReturnsTrue() {
        CreateEventRequestDto request = new CreateEventRequestDto("T", List.of(), "D", null, List.of(), 1L, 1L, true);
        assertTrue(validator.isValid(request, null));
    }
}