package greencity.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserAndFriendsWithOnlineStatusDto {
    @NotNull
    private UserWithOnlineStatusDto user;

    @NotNull
    private List<UserWithOnlineStatusDto> friends;
}
