package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoResponseMapperTest {

    @Test
    void convert_shouldMapEntityToDtoResponse() {
        Category category = new Category();
        category.setId(2L);

        CategoryDtoResponseMapper mapper = new CategoryDtoResponseMapper();

        CategoryDtoResponse dto = mapper.convert(category);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
    }

    @Test
    void convert_nullInput_shouldThrowNullPointerException() {
        CategoryDtoResponseMapper mapper = new CategoryDtoResponseMapper();

        assertThrows(NullPointerException.class, () -> mapper.convert((Category) null));
    }
}