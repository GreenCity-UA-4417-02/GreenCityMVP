package greencity.dto.habit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class HabitEnrollDto {
    private Long habitAssignId;
    private String habitName;
    private String habitDescription;
    private boolean isEnrolled;
}
