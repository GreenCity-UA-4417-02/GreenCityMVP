package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.EventDateDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidEventTimeValidatorTest {
    private final ValidEventTimeValidator validator = new ValidEventTimeValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void isValid_TodayWithPastTime_ReturnsFalse() {
        EventDateDto dto = new EventDateDto(
                LocalDate.now(),
                LocalTime.now().minusHours(1),
                LocalTime.now().plusHours(1),
                false, null, null
        );

        assertFalse(validator.isValid(dto, context));
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValid_FutureDateAnyTime_ReturnsTrue() {
        EventDateDto dto = ModelUtils.getEventDateDto();
        assertTrue(validator.isValid(dto, context));
    }

    @Test
    void isValid_NullDto_ReturnsTrue() {
        assertTrue(validator.isValid(null, context));
    }
}