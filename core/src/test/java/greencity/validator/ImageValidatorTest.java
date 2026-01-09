package greencity.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {
    @Mock
    private MultipartFile multipartFile;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private ImageValidator imageValidator;

    @Test
    void isValid_NullImage_ReturnsTrue() {
        boolean result = imageValidator.isValid(null, context);
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "image/png", "image/jpg"})
    void isValid_ValidMimeTypes_ReturnsTrue(String mimeType) {
        when(multipartFile.getContentType()).thenReturn(mimeType);

        boolean result = imageValidator.isValid(multipartFile, context);

        assertTrue(result);
    }
}