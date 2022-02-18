package com.kyuwon.booklog.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserData;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                userSaveRequestData = getUserSaveData();
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

        @BeforeEach
        void createUser() throws Exception {
            user = prepareUser(getUserSaveData());
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
                                .content(objectMapper.writeValueAsString(userUpdatedData)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(NEW_NAME)))
                        .andExpect(jsonPath("$.email", is(user.getEmail())));
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

    private UserSaveRequestData getUserSaveData() {
        return UserSaveRequestData.builder()
                .email(EMAIL)
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

}
