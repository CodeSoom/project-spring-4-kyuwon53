package com.kyuwon.booklog.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.session.SessionResponseData;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("사용자 컨트롤러")
class UserControllerTest {
    private static final String NAME = "테스트 이름";
    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "1234abcd*";
    private static final String PICTURE = "테스트 사진";

    private static final String NEW_NAME = "NEW테스트 이름";
    private static final String NEW_PASSWORD = "NEW1234abcd*";
    private static final String NEW_PICTURE = "NEW테스트 사진";

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
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("사용자 회원가입 요청은")
    class Describe_signUp {
        UserSaveRequestData userSaveRequestData;

        @Nested
        @DisplayName("사용자 회원가입 정보가 주어진다면")
        class Context_with_user_data {
            @BeforeEach
            void setUp() {
                userSaveRequestData = getUserSaveData("new");
            }

            @Test
            @DisplayName("사용자를 저장하고 Created를 응답한다.")
            void it_response_status_created() throws Exception {
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userSaveRequestData)))
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("사용자 수정 요청은")
    class Describe_update {
        UserData userUpdatedData;
        User user;
        SessionResponseData sessionResponseData;

        @BeforeEach
        void setUser() throws Exception {
            user = prepareUser(getUserSaveData("update"));

            sessionResponseData = login(getLoginData(user));
        }

        @Nested
        @DisplayName("존재하는 사용자일 경우")
        class Context_when_exist_user {

            @BeforeEach
            void setUp() {
                userUpdatedData = getModifyUserData(user.getEmail());
            }

            @Test
            @DisplayName("정보를 수정하고 OK를 응답한다.")
            void it_response_status_ok() throws Exception {
                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdatedData))
                                .header("Authorization",
                                        String.format("Bearer %s", sessionResponseData.getAccessToken())
                                )
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(NEW_NAME)))
                        .andExpect(jsonPath("$.email", is(user.getEmail())));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 사용자일 경우")
        class Context_when_not_exist_user {
            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("찾을 수 없는 사용자라고 예외를 던진다.")
            void it_throw_UserNotFoundException() throws Exception {
                userUpdatedData = getModifyUserData(user.getEmail());

                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdatedData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("다른 사람 토큰일 경우")
        class Context_when_invalid_token {

            @BeforeEach
            void setUp() {
                userUpdatedData = getModifyUserData(user.getEmail());
            }

            @Test
            @DisplayName("isUnauthorized 응답한다.")
            void it_response_status_isUnauthorized() throws Exception {
                mockMvc.perform(patch("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdatedData))
                                .header("Authorization",
                                        String.format("Bearer %s", sessionResponseData.getAccessToken() + "xxxx")
                                )
                        )
                        .andDo(print())
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("사용자 삭제 요청은")
    class Describe_delete {
        User user;
        SessionResponseData sessionResponseData;

        @BeforeEach
        void createUser() throws Exception {
            user = prepareUser(getUserSaveData("delete"));

            sessionResponseData = login(getLoginData(user));
        }

        @Nested
        @DisplayName("존재하는 사용자일 경우")
        class Context_when_exist_user {

            @Test
            @DisplayName("HTTP NoContent를 응답한다.")
            void it_response_status_NoContent() throws Exception {
                mockMvc.perform(delete("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(user.getEmail())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken()))
                        .andDo(print())
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 사용자일 경우")
        class Context_when_not_exist_user {
            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("찾을 수 없는 사용자라고 예외를 던진다.")
            void it_throw_UserNotFoundException() throws Exception {
                mockMvc.perform(delete("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(user.getEmail())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken()))
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("잘못된 인증정보로 요청할 경우")
        class Context_when_wrong_accesstoken {

            @Test
            @DisplayName("HTTP NoContent를 응답한다.")
            void it_response_status_isUnauthorized() throws Exception {
                mockMvc.perform(delete("/users/" + user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(user.getEmail())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xx"))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("사용자 상세조회 요청은")
    class Describe_detail {
        User user;

        @BeforeEach
        void createUser() throws Exception {
            user = prepareUser(getUserSaveData("detail"));
        }

        @Nested
        @DisplayName("존재하는 사용자일 경우")
        class Context_when_exist_user {

            @Test
            @DisplayName("HTTP isOk를 응답한다.")
            void it_response_status_isOk() throws Exception {
                mockMvc.perform(get("/users/" + user.getId()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(NAME)))
                        .andExpect(jsonPath("$.email", is("detail" + EMAIL)));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 사용자일 경우")
        class Context_when_not_exist_user {
            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("찾을 수 없는 사용자라고 예외를 던진다.")
            void it_throw_UserNotFoundException() throws Exception {
                mockMvc.perform(get("/users/" + user.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("사용자 목록조회 요청은")
    class Describe_list {

        @Nested
        @DisplayName("사용자가 존재할 경우")
        class Context_when_exist_user {
            int userCount = 10;
            List<User> userList = new ArrayList<>();

            @BeforeEach
            void createUser() throws Exception {
                for (int i = 0; i < userCount; i++) {
                    prepareUser(getUserSaveData("" + i));
                    userList.add(getUserSaveData("" + i).toEntity());
                }
            }

            @Test
            @DisplayName("HTTP isOk를 응답한다.")
            void it_response_status_isOk() throws Exception {
                mockMvc.perform(get("/users"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(NAME)));
            }
        }

        @Nested
        @DisplayName("사용자가 존재하지 않을 경우")
        class Context_when_not_exist_user {
            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("빈 배열을 리턴한다.")
            void it_return_empty_array() throws Exception {
                mockMvc.perform(get("/users"))
                        .andExpect(content().string(containsString("[]")));
            }
        }
    }

    private UserData getModifyUserData(String email) {
        return UserData.builder()
                .email(email)
                .name(NEW_NAME)
                .password(NEW_PASSWORD)
                .picture(NEW_PICTURE)
                .build();
    }

    private UserSaveRequestData getUserSaveData(String prefix) {
        return UserSaveRequestData.builder()
                .email(prefix + EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .picture(PICTURE)
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

    private SessionResponseData login(UserLoginData userLoginData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginData)));

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, SessionResponseData.class);
    }

    private UserLoginData getLoginData(User user) {
        return UserLoginData.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
