package greencity.dto.habitstatuscalendar;

import greencity.dto.habit.HabitAssignVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class HabitStatusCalendarVO {
    private Long id;
    private LocalDate enrollDate;
    private HabitAssignVO habitAssignVO;
}
