package personal.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import personal.sns.controller.request.UserJoinRequest;
import personal.sns.controller.request.UserLoginRequest;
import personal.sns.exception.ErrorCode;
import personal.sns.exception.SnsApplicationException;
import personal.sns.model.User;
import personal.sns.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void 회원가입() throws Exception {
        String userName = "userName";
        String password = "password";

        //TODO: develop
        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO: add request body
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는_경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        //TODO: develop
        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO: add request body
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void 로그인() throws Exception {
        String userName = "userName";
        String password = "password";

        //TODO: develop
        when(userService.login(userName, password)).thenReturn("test_token");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO: add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인시_회원가입이_안된_userName을_입력할_경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        //TODO: develop
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO: add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void 로그인시_틀린_패스워드를_입력할_경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        //TODO: develop
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // TODO: add request body
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}