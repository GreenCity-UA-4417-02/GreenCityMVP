package greencity.dto.ratingstatistics;

import greencity.annotations.RatingCalculationEnum;
import greencity.dto.user.UserVO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RatingStatisticsDto {
    private Long id;
    private ZonedDateTime createDate;
    private RatingCalculationEnum ratingCalculationEnum;
    private float pointsChanged;
    private float rating;
    private UserVO user;
}
