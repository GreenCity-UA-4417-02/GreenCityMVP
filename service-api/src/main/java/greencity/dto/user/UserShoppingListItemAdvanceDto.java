package greencity.dto.user;

import greencity.enums.ShoppingListItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserShoppingListItemAdvanceDto {
    private Long id;
    private Long shoppingListItemId;
    private ShoppingListItemStatus status;
    private LocalDateTime dateCompleted;
    private String content;

    /**
     * Method returns status in Boolean.
     */
    public Boolean getBoolStatus() {
        return status.equals(ShoppingListItemStatus.INPROGRESS);
    }
}
