package com.kyuwon.booklog.utils;

import com.kyuwon.booklog.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("토큰 관리 ")
@SpringBootTest
class WebTokenUtilTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9." +
            "eaz-MEap3WlSgb38qIRgNEynf4X7KHVy0i3NyjHJO5E";
    private static final String INVALID_TOKEN = VALID_TOKEN + "xxx";

    @Autowired
    private WebTokenUtil webTokenUtil;

    @Nested
    @DisplayName("인코딩은")
    class Describe_encode {
        @Nested
        @DisplayName("id가 주어지면")
        class Context_with_id {
            @Test
            @DisplayName("유효한 토큰을 리턴한다.")
            void it_return_Signed_token() {
                String token = webTokenUtil.encode(1L);

                assertThat(token).isEqualTo(VALID_TOKEN);
            }
        }
    }

    @Nested
    @DisplayName("디코딩은")
    class Describe_decode {
        @Nested
        @DisplayName("유효한 토큰이 주어지면")
        class Context_with_valid_token {
            @Test
            @DisplayName("해당 id를 리턴한다.")
            void it_return_id() {
                Claims claims = webTokenUtil.decode(VALID_TOKEN);

                assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰이 주어지면")
        class Context_with_invalid_token {
            @Test
            @DisplayName("서명되지 않았다는 예외를 던집니다.")
            void it_throw_SignatureException() {
                assertThatThrownBy(() -> webTokenUtil.decode(INVALID_TOKEN))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }
    }
}
