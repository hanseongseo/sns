package personal.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import personal.sns.model.User;
import personal.sns.model.UserRole;

@Getter
@AllArgsConstructor
public class UserLoginResponse {
    private String token;

}
