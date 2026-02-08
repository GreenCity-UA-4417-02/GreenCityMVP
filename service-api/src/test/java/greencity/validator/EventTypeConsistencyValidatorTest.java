package greencity.validator;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateDto;
import greencity.enums.EventType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventTypeConsistencyValidatorTest {
    @InjectMocks
    private EventTypeConsistencyValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void isValid_OfflineWithoutAddress_ReturnsFalse() {
        EventDateDto dateDto = new EventDateDto(null, null, null, false, null, null);
        CreateEventRequestDto request = new CreateEventRequestDto("Title", List.of(dateDto), "Desc", EventType.OFFLINE, List.of(), 1L, 1L, true);

        assertFalse(validator.isValid(request, context));
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_OnlineWithoutLink_ReturnsFalse() {
        EventDateDto dateDto = new EventDateDto(null, null, null, false, null, null);
        CreateEventRequestDto request = new CreateEventRequestDto("Title", List.of(dateDto), "Desc", EventType.ONLINE, List.of(), 1L, 1L, true);

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void isValid_OnlineOfflineValid_ReturnsTrue() {
        CreateEventRequestDto request = ModelUtils.getCreateEventRequestDto(); // В ModelUtils вже ONLINE_OFFLINE з обома полями
        assertTrue(validator.isValid(request, context));
    }

    @Test
    void isValid_NullValues_ReturnsTrue() {
        assertTrue(validator.isValid(null, context));
    }
}