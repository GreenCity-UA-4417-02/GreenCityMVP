package greencity.dto.shoppinglistitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItemResponseDto {
    private Long id;

    private List<ShoppingListItemTranslationDTO> translations;
}
