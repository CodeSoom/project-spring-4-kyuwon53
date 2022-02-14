package com.kyuwon.booklog.controller.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("세션 요청")
class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Nested
    @DisplayName("로그인 요청은")
    class Describe_login {
        @Nested
        @DisplayName("")
        class Context {
            @Test
            @DisplayName("토큰과 201 Created HTTP 상태코드를 응답한다.")
            void it_respose_created_with_token() throws Exception {
                mockMvc.perform(post("/session"))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken", matchesPattern(TOKEN_REGEX)));
                ;
            }
        }
    }
}
