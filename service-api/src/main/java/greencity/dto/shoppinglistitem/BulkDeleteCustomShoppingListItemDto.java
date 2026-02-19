package greencity.dto.shoppinglistitem;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkDeleteCustomShoppingListItemDto {
    @Valid
    List<@Valid CustomShoppingListItemRequestDto> customShoppingListItems;
}
