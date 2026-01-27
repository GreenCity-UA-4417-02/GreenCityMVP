package greencity.validator;

import greencity.annotations.ValidLanguage;
import greencity.service.LanguageService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {
    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private LanguageValidator languageValidator;

    private List<String> supportedLanguages;

    @BeforeEach
    void setUp() {
        supportedLanguages = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(supportedLanguages);

        languageValidator.initialize(mock(ValidLanguage.class));
    }

    @Test
    void isValid_WithSupportedLanguage_ReturnsTrue() {
        Locale locale = Locale.of("en");

        boolean result = languageValidator.isValid(locale, context);
        assertTrue(result);
    }

    @Test
    void isValid_WithUnsupportedLanguage_ReturnsFalse() {
        Locale locale = Locale.of("fr");

        boolean result = languageValidator.isValid(locale, context);
        assertFalse(result);
    }

    @Test
    void isValid_WithNullLocale_ReturnsFalse() {
        assertThrows(NullPointerException.class, () -> languageValidator.isValid(null, context));
    }

    @Test
    void isValid_WithEmptyLanguageCode_ReturnsFalse() {
        Locale locale = Locale.of("");

        boolean result = languageValidator.isValid(locale, context);
        assertFalse(result);
    }

    @Test
    void initialize_WithEmptyServiceList_ShouldHandleGracefully() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of());
        languageValidator.initialize(mock(ValidLanguage.class));

        Locale locale = Locale.of("en");

        boolean result = languageValidator.isValid(locale, context);
        assertFalse(result);
    }
}
