package greencity.validator;

import greencity.ModelUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidEventTypeConsistencyValidatorTest {
    private final ValidEventTypeConsistencyValidator validator = new ValidEventTypeConsistencyValidator();

    @Test
    void isValid_AlwaysReturnsTrue() {
        assertTrue(validator.isValid(ModelUtils.getEventDateDto(), null));
        assertTrue(validator.isValid(null, null));
    }
}