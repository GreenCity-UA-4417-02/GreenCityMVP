package greencity.dto.habitfact;

import greencity.dto.habit.HabitVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactDtoResponse {
    private Long id;

    private List<HabitFactTranslationDto> translations;

    private HabitVO habit;
}
