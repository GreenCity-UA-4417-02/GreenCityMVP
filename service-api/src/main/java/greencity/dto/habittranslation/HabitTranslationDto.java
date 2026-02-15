package greencity.dto.habittranslation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitTranslationDto implements Serializable {
    private String description;
    private String habitItem;
    private String languageCode;
    private String name;
}
