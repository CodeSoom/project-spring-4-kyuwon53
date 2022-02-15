package com.kyuwon.booklog.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("토큰 관리 ")
@SpringBootTest
class WebTokenUtilTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
           "eyJ1c2VySWQiOjF9." +
           "eaz-MEap3WlSgb38qIRgNEynf4X7KHVy0i3NyjHJO5E";

    @Autowired
    private WebTokenUtil webTokenUtil;

    @Nested
    @DisplayName("인코딩은")
    class Describe_encode {
        @Nested
        @DisplayName("id가 주어지면")
        class Context_with_id {
            @Test
            @DisplayName("인증된 토큰을 리턴한다.")
            void it_return_Signed_token() {
                String acceessToken = webTokenUtil.encode(1L);

                assertThat(acceessToken).isEqualTo(ACCESS_TOKEN);
            }
        }
    }
}
