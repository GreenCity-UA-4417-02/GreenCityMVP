package greencity.dto.language;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageTranslationDTO {
    @Valid
    @NotNull
    protected LanguageDTO language;

    @Size(min = ServiceValidationConstants.ADVICE_MIN_LENGTH, max = ServiceValidationConstants.ADVICE_MAX_LENGTH)
    private String content;
}
