package greencity.dto.habit;

import greencity.dto.user.UserShoppingListItemAdvanceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UpdateUserShoppingListDto {
    private Long habitAssignId;
    private Long userShoppingListItemId;
    private List<UserShoppingListItemAdvanceDto> userShoppingListAdvanceDto;
}
