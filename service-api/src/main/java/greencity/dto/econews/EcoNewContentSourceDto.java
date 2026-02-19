package greencity.dto.econews;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@EqualsAndHashCode
public class EcoNewContentSourceDto {
    @NotEmpty
    private String content;

    private String source;
}
