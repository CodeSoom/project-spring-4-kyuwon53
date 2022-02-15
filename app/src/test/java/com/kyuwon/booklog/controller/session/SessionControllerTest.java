package com.kyuwon.booklog.controller.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("세션 요청")
class SessionControllerTest {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("로그인 요청은")
    class Describe_login {
        @Nested
        @DisplayName("올바른 사용자 정보가 주어지면")
        class Context {
            private UserLoginData userLoginData;
            private User user;

            @BeforeEach
            void setUp() throws Exception {
                userRepository.deleteAll();

                user = prepareUser(getUserSaveData());
                userLoginData = UserLoginData.builder()
                        .email("test@email.com")
                        .password("1234test*")
                        .build();
            }

            @Test
            @DisplayName("토큰과 201 Created HTTP 상태코드를 응답한다.")
            void it_respose_created_with_token() throws Exception {
                mockMvc.perform(post("/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userLoginData)))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.accessToken", matchesPattern(TOKEN_REGEX)));
                ;
            }
        }
    }

    private UserSaveRequestData getUserSaveData() {
        return UserSaveRequestData.builder()
                .email("test@email.com")
                .name("테스트 이름")
                .password("1234abcd*")
                .picture("테스트 사진")
                .build();
    }

    private User prepareUser(UserSaveRequestData saveRequestData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveRequestData)));

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, User.class);
    }
}
