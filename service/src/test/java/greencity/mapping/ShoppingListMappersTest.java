package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingListMappersTest {

    @Test
    void shoppingListItemDtoMapper_shouldMapEntityToDto() {
        ShoppingListItem item = new ShoppingListItem();
        item.setId(5L);

        ShoppingListItemTranslation translation = new ShoppingListItemTranslation();
        translation.setShoppingListItem(item);
        translation.setContent("Milk");

        ShoppingListItemDtoMapper mapper = new ShoppingListItemDtoMapper();

        ShoppingListItemDto dto = MapperTestUtils.convert(mapper, translation);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Milk", extractStringValue(dto));
    }

    @Test
    void shoppingListItemDtoMapper_shouldReturnNullWhenSourceIsNull() {
        ShoppingListItemDtoMapper mapper = new ShoppingListItemDtoMapper();
        ShoppingListItemDto dto = MapperTestUtils.convertNull(mapper);
        assertNull(dto);
    }

    private String extractStringValue(Object dto) {
        String[] getterCandidates = {"getContent", "getText", "getName", "getTitle", "getLabel"};
        for (String getter : getterCandidates) {
            try {
                Method m = dto.getClass().getMethod(getter);
                Object val = m.invoke(dto);
                if (val instanceof String) {
                    return (String) val;
                }
            } catch (Exception ignored) {
            }
        }

        String[] fieldCandidates = {"content", "text", "name", "title", "label"};
        for (String fieldName : fieldCandidates) {
            try {
                Field f = dto.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Object val = f.get(dto);
                if (val instanceof String) {
                    return (String) val;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}