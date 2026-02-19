package greencity.dto.tag;

import greencity.dto.language.LanguageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TagTranslationVO {
    private Long id;
    private String name;
    private LanguageVO languageVO;
}
