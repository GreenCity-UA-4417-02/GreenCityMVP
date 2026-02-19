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
public class EcoNewsCommentAuthorDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String userProfilePicturePath;
}
