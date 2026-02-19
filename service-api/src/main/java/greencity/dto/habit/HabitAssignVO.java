package greencity.dto.habit;

import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class HabitAssignVO {
    private Long id;
    private HabitAssignStatus status;
    private ZonedDateTime createDateTime;
    private HabitVO habitVO;
    private UserVO userVO;
    private Integer duration;
    private Integer habitStreak;
    private Integer workingDays;
    private ZonedDateTime lastEnrollmentDate;
}
