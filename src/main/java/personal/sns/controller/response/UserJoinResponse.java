package personal.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import personal.sns.model.User;
import personal.sns.model.UserRole;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUserName(),
                user.getRole()
        );
    }
}
