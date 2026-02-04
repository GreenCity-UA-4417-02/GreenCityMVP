package greencity.mapping;

import greencity.mapping.MultipartBase64ImageMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsMappersTest {

    @Test
    void multipartBase64ImageMapper_nullInput() {
        MultipartBase64ImageMapper mapper = new MultipartBase64ImageMapper();
        assertNull(mapper.convert((String) null));
    }
}