package greencity.dto.habitfact;

import greencity.dto.language.LanguageDTO;
import greencity.enums.FactOfDayStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitFactTranslationUpdateDto {
    @NotNull
    private FactOfDayStatus factOfDayStatus;

    @NotNull
    private LanguageDTO language;

    @NotBlank
    private String content;
}
