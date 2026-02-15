package greencity.dto.ratingstatistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class RatingStatisticsViewDto {
    private String id;
    private String eventName;
    private String userId;
    private String userEmail;
    private String startDate;
    private String endDate;
    private String pointsChanged;
    private String currentRating;
}
