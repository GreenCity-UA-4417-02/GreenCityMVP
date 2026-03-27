package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.ShoppingListItem;
import greencity.entity.Tag;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HabitMappersTest {
    @Test
    void habitDtoMapper_shouldMapEntityToDto() {
        Language en = Language.builder()
                .id(1L)
                .code("en")
                .build();

        TagTranslation tagTranslationEn = TagTranslation.builder()
                .id(10L)
                .name("Eco")
                .language(en)
                .build();

        Tag tag = Tag.builder()
                .id(100L)
                .tagTranslations(List.of(tagTranslationEn))
                .build();

        ShoppingListItemTranslation itemTranslationEn = ShoppingListItemTranslation.builder()
                .id(20L)
                .language(en)
                .content("Bottle")
                .build();

        ShoppingListItem item = ShoppingListItem.builder()
                .id(200L)
                .translations(List.of(itemTranslationEn))
                .build();

        Habit habit = Habit.builder()
                .id(300L)
                .image("img.png")
                .defaultDuration(7)
                .complexity(3)
                .tags(Set.of(tag))
                .shoppingListItems(Set.of(item))
                .build();

        HabitTranslation ht = HabitTranslation.builder()
                .id(400L)
                .language(en)
                .habit(habit)
                .name("Drink water")
                .description("Desc")
                .habitItem("Item")
                .build();

        HabitDtoMapper mapper = new HabitDtoMapper();

        HabitDto dto = MapperTestUtils.convert(mapper, ht);

        assertNotNull(dto);
        assertEquals(300L, dto.getId());
        assertEquals("img.png", dto.getImage());
        assertEquals(7, dto.getDefaultDuration());
        assertEquals(3, dto.getComplexity());

        assertNotNull(dto.getHabitTranslation());
        assertEquals("Drink water", dto.getHabitTranslation().getName());
        assertEquals("Desc", dto.getHabitTranslation().getDescription());
        assertEquals("Item", dto.getHabitTranslation().getHabitItem());
        assertEquals("en", dto.getHabitTranslation().getLanguageCode());

        assertNotNull(dto.getTags());
        assertEquals(List.of("Eco"), dto.getTags());

        assertNotNull(dto.getShoppingListItems());
        assertEquals(1, dto.getShoppingListItems().size());
        assertEquals(200L, dto.getShoppingListItems().get(0).getId());
        assertEquals("Bottle", dto.getShoppingListItems().get(0).getText());
        assertNotNull(dto.getShoppingListItems().get(0).getStatus());
    }

    @Test
    void habitDtoMapper_shouldThrowWhenSourceIsNull() {
        HabitDtoMapper mapper = new HabitDtoMapper();
        assertThrows(NullPointerException.class, () -> MapperTestUtils.convert(mapper, null));
    }

    @Test
    void habitDtoMapper_shouldReturnEmptyShoppingListWhenHabitShoppingListIsNull() {
        Language en = Language.builder().id(1L).code("en").build();

        Habit habit = Habit.builder()
                .id(1L)
                .image("img.png")
                .defaultDuration(5)
                .complexity(1)
                .tags(Set.of())
                .shoppingListItems(null)
                .build();

        HabitTranslation ht = HabitTranslation.builder()
                .language(en)
                .habit(habit)
                .name("n")
                .description("d")
                .habitItem("i")
                .build();

        HabitDtoMapper mapper = new HabitDtoMapper();

        HabitDto dto = MapperTestUtils.convert(mapper, ht);

        assertNotNull(dto);
        assertNotNull(dto.getShoppingListItems());
        assertTrue(dto.getShoppingListItems().isEmpty());
    }
}