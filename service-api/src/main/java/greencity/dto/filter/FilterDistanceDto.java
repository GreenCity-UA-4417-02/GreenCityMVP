package greencity.dto.filter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static greencity.constant.ServiceValidationConstants.LAT_MAX_VALIDATION;
import static greencity.constant.ServiceValidationConstants.LAT_MIN_VALIDATION;
import static greencity.constant.ServiceValidationConstants.LNG_MAX_VALIDATION;
import static greencity.constant.ServiceValidationConstants.LNG_MIN_VALIDATION;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDistanceDto {
    @Min(value = -90, message = LAT_MIN_VALIDATION)
    @Max(value = 90, message = LAT_MAX_VALIDATION)
    private Double lat;
    @Min(value = -180, message = LNG_MIN_VALIDATION)
    @Max(value = 180, message = LNG_MAX_VALIDATION)
    private Double lng;
    @Positive
    private Double distance;
}
