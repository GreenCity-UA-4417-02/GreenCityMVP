package greencity.validator;

import greencity.constant.ValidationConstants;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EcoNewsDtoRequestValidatorTest {

    private EcoNewsDtoRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EcoNewsDtoRequestValidator();
    }

    @Test
    void isValid_returnsTrue_whenTagsValid_andSourceNull() {
        AddEcoNewsDtoRequest request = buildRequest(null, List.of("tag1"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValid_returnsTrue_whenTagsValid_andSourceEmpty() {
        AddEcoNewsDtoRequest request = buildRequest("", List.of("tag1", "tag2"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValid_returnsTrue_whenTagsAtMax_andSourceValidUrl() {
        AddEcoNewsDtoRequest request = buildRequest("https://eco-lavca.ua/", List.of("t1", "t2", "t3"));
        assertTrue(validator.isValid(request, null));
    }

    @Test
    void isValid_throwsWrongCountOfTagsException_whenTagsEmpty() {
        AddEcoNewsDtoRequest request = buildRequest("https://eco-lavca.ua/", Collections.emptyList());

        assertThrows(WrongCountOfTagsException.class,
            () -> validator.isValid(request, null));
    }

    @Test
    void isValid_throwsWrongCountOfTagsException_whenTagsMoreThanMax() {
        int max = ValidationConstants.MAX_AMOUNT_OF_TAGS;
        // guarantee > max
        List<String> tags = List.of("t1", "t2", "t3", "t4");

        assertTrue(tags.size() > max, "Test setup error: tags size must be > MAX_AMOUNT_OF_TAGS");

        AddEcoNewsDtoRequest request = buildRequest("https://eco-lavca.ua/", tags);

        assertThrows(WrongCountOfTagsException.class,
            () -> validator.isValid(request, null));
    }

    @Test
    void isValid_throwsInvalidURLException_whenSourcePresentButInvalid() {
        AddEcoNewsDtoRequest request = buildRequest("not-a-url", List.of("tag1"));

        assertThrows(InvalidURLException.class,
            () -> validator.isValid(request, null));
    }

    private AddEcoNewsDtoRequest buildRequest(String source, List<String> tags) {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest();
        request.setSource(source);
        request.setTags(tags);
        return request;
    }
}