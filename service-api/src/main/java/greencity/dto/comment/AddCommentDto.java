package greencity.dto.comment;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.rate.EstimateAddDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    @NotBlank
    @Length(min = ServiceValidationConstants.COMMENT_MIN_LENGTH, max = ServiceValidationConstants.COMMENT_MAX_LENGTH)
    private String text;
    @Valid
    private EstimateAddDto estimate;
}
