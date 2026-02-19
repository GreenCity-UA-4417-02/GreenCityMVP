package greencity.dto.habit;

import greencity.constant.AppConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class HabitAssignPropertiesDto {
    @NotNull
    @Min(AppConstant.MIN_DAYS_DURATION)
    @Max(AppConstant.MAX_DAYS_DURATION)
    private Integer duration;

    private List<Long> defaultShoppingListItems;
}
