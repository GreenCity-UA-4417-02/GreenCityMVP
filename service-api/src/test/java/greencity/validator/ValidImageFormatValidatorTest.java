package greencity.validator;

import greencity.annotations.ValidImageFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidImageFormatValidatorTest {
    private final ValidImageFormatValidator validator = new ValidImageFormatValidator();

    @Mock
    private ValidImageFormat annotation;

    @BeforeEach
    void setUp() {
        when(annotation.allowedFormats()).thenReturn(new String[]{"jpg", "jpeg", "png"});
        validator.initialize(annotation);
    }

    @Test
    void isValid_CorrectFormat_ReturnsTrue() {
        assertTrue(validator.isValid("https://example.com/photo.png", null));
        assertTrue(validator.isValid("https://example.com/photo.JPG", null));
    }

    @Test
    void isValid_InCorrectFormat_ReturnsFalse() {
        assertFalse(validator.isValid("https://example.com/photo.gif", null));
        assertFalse(validator.isValid("https://example.com/photo.pdf", null));
    }

    @Test
    void isValid_Null_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }
}