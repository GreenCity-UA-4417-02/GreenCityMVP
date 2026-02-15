package greencity.dto.habitstatistic;

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
public class GetHabitStatisticDto {
    Long amountOfUsersAcquired;
    List<HabitStatisticDto> habitStatisticDtoList;
}
