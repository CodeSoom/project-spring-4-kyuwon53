package com.kyuwon.booklog.service.session;

import com.kyuwon.booklog.domain.user.Role;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.errors.LoginFailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("회원 인증 처리")
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserRepository userRepository;

    public static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "testpassword123*";

    private User user;

    @BeforeEach
    void setUserData() {
        user = User.builder()
                .email(EMAIL)
                .name("테스트")
                .password(PASSWORD)
                .picture("test")
                .role(Role.USER)
                .build();
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("로그인은")
    class Discribe_login {

        @BeforeEach
        void signUpUser() {
            userRepository.save(user);
        }

        @Nested
        @DisplayName("유효한 정보로 요청하면")
        class Context_with {
            UserLoginData userLoginData;


            @BeforeEach
            void setUp() {
                userLoginData = UserLoginData.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .build();
            }

            @Test
            @DisplayName("토큰을 리턴한다.")
            void it_return_access_Token() {
                String accessToken = authenticationService.login(userLoginData);

                assertThat(accessToken).matches(TOKEN_REGEX);
            }
        }

        @Nested
        @DisplayName("잘못된 이메일로 요청하면")
        class Context_with_Wrong_Email {
            UserLoginData userWrongLoginData;

            @BeforeEach
            void setLoginData() {
                userWrongLoginData = UserLoginData.builder()
                        .email("wrong" + user.getEmail())
                        .password(user.getPassword())
                        .build();
            }

            @Test
            @DisplayName("로그인에 실패했다는 예외를 던진다.")
            void it_throw_loginFailException() {
                assertThatThrownBy(
                        () -> authenticationService.login(userWrongLoginData)
                ).isInstanceOf(LoginFailException.class);
            }
        }
    }
}
