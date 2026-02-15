package greencity.dto.shoppinglistitem;

import greencity.enums.ShoppingListItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ShoppingListItemWithStatusRequestDto extends ShoppingListItemRequestDto {
    @NotNull
    private ShoppingListItemStatus status;
}
