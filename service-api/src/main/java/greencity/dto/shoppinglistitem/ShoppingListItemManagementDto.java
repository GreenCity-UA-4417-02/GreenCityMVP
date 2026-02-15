package greencity.dto.shoppinglistitem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ShoppingListItemManagementDto {
    @NotNull
    private Long id;

    private List<ShoppingListItemTranslationVO> translations;
}
