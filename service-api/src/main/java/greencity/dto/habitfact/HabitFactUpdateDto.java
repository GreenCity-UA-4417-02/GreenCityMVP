package greencity.dto.habitfact;

import greencity.dto.user.HabitIdRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class HabitFactUpdateDto {
    @Valid
    private List<HabitFactTranslationUpdateDto> translations;

    @Valid
    private HabitIdRequestDto habit;
}
