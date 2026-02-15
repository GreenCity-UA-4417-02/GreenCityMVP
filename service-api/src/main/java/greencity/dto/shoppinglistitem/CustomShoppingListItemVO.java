package greencity.dto.shoppinglistitem;

import greencity.dto.user.UserShoppingListItemVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomShoppingListItemVO {
    private Long id;

    private String text;

    private List<UserShoppingListItemVO> userShoppingListItems = new ArrayList<>();
}
