package greencity.validator;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FutureOrPresentDateValidatorTest {
    private final FutureOrPresentDateValidator validator = new FutureOrPresentDateValidator();

    @Test
    void isValid_PastDate_ReturnsFalse() {
        assertFalse(validator.isValid(LocalDate.now().minusDays(1), null));
    }

    @Test
    void isValid_TodayDate_ReturnsTrue() {
        assertTrue(validator.isValid(LocalDate.now(), null));
    }

    @Test
    void isValid_FutureDate_ReturnsTrue() {
        assertTrue(validator.isValid(LocalDate.now().plusDays(10), null));
    }

    @Test
    void isValid_NullDate_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }
}