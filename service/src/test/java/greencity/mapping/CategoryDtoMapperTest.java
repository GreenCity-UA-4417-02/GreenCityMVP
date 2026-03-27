package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoMapperTest {

    @Test
    void convert_shouldMapDtoToEntity() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Plastic");
        dto.setId(2L);

        CategoryDtoMapper mapper = new CategoryDtoMapper();

        Category category = MapperTestUtils.convert(mapper, dto);

        assertNotNull(category);
        assertEquals("Plastic", category.getName());
    }

    @Test
    void convert_nullInput_shouldThrowNullPointerException() {
        CategoryDtoMapper mapper = new CategoryDtoMapper();

        assertThrows(
                NullPointerException.class,
                () -> mapper.convert((CategoryDto) null)
        );
    }
}