package greencity.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserFilterDtoResponse {
    private Long id;
    private String name;
    private String searchCriteria;
    private String userStatus;
    private String userRole;
}
