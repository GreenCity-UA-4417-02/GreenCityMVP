package greencity.dto.econewscomment;

import jakarta.validation.constraints.NotEmpty;
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
@Builder
@EqualsAndHashCode
public class AmountCommentLikesDto {
    @NotEmpty
    private Long id;

    private Integer amountLikes;

    private Long userId;

    private boolean isLiked;
}
