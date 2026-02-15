package greencity.dto.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BulkSaveUserShoppingListItemDto {
    @Valid
    List<@Valid UserShoppingListItemDto> userShoppingListItems;
}
