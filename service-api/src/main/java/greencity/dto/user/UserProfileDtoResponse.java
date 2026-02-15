package greencity.dto.user;

import greencity.enums.Role;
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
public class UserProfileDtoResponse {
    private String profilePicturePath;
    private String name;
    private String city;
    private String userCredo;
    private Boolean showLocation;
    private Boolean showEcoPlace;
    private Boolean showShoppingList;
    private Float rating;
    private Role role;
}
