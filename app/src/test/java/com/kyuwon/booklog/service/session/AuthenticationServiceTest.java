package com.kyuwon.booklog.service.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("회원 인증 처리")
class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;

    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Nested
    @DisplayName("로그인은")
    class Discribe_login {
        @Nested
        @DisplayName("유효한 정보로 요청하면")
        class Context_with {
            @Test
            @DisplayName("토큰을 리턴한다.")
            void it_return_access_Token() {
                String accessToken = authenticationService.login();

                assertThat(accessToken).matches(TOKEN_REGEX);
            }
        }
    }
}
