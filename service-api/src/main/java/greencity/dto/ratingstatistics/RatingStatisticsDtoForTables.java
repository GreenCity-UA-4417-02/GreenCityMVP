package greencity.dto.ratingstatistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RatingStatisticsDtoForTables {
    private Long id;
    private ZonedDateTime createDate;
    private String eventName;
    private float pointsChanged;
    private float rating;
    private long userId;
    private String userEmail;
}
