package com.kyuwon.booklog.controller.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import com.kyuwon.booklog.dto.session.SessionResponseData;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("????????? ?????? ????????????")
class PostsControllerTest {
    private static final String TITLE = "????????? ??????";
    private static final String CONTENT = "????????? ??????";
    private static final String AUTHOR = "????????? ??????";
    private static final String EMAIL = "test@email.com";
    private static final String NEW_TITLE = "????????? ??????";
    private static final String NEW_CONTENT = "????????? ??????";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext wac;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private SessionResponseData sessionResponseData;

    @BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        userRepository.deleteAll();

        user = prepareUser(getUserSaveData());

        sessionResponseData = login(getLoginData(user));
    }

    @Nested
    @DisplayName("????????? ?????? ?????? ?????????")
    class Decribe_GET_List {
        @Nested
        @DisplayName("????????? ???????????? ?????????")
        class Context_exist_post {
            final int postCount = 10;
            List<Posts> postsList = new ArrayList<>();

            @BeforeEach
            void setUp() throws Exception {

                for (int i = 0; i < postCount; i++) {
                    Posts post = preparePost(getPostSaveData());
                    postsList.add(getPostSaveData().toEntity());
                }
            }

            @Test
            @DisplayName("?????? ???????????? ????????????.")
            void it_return_list() throws Exception {
                mockMvc.perform(get("/posts"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(TITLE)));
            }
        }

        @Nested
        @DisplayName("????????? ???????????? ?????????")
        class Context_not_exist_post {
            private static final String EMPTY_LIST = "[]";

            @BeforeEach
            void setEmptyList() throws Exception {
                List<Posts> postsList = objectMapper.convertValue(
                        getPostList(),
                        new TypeReference<List<Posts>>() {
                        });

                postsList.forEach(post -> {
                    try {
                        deletePostBeforeTest(post.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Test
            @DisplayName("??? ???????????? ????????????.")
            void it_return_empty_list() throws Exception {
                mockMvc.perform(get("/posts"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(EMPTY_LIST)));
            }
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????? ?????????")
    class Decribe_GET {
        private Posts post;

        @BeforeEach
        void setUp() throws Exception {
            post = preparePost(getPostSaveData());
        }

        @Nested
        @DisplayName("id??? ???????????? ???????????? ?????????")
        class Context_exist_id_post {
            @DisplayName("????????? ????????? 200 ok HTTP ??????????????? ????????????.")
            @Test
            void it_response_post() throws Exception {
                mockMvc.perform(get("/posts/" + post.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.title", is(post.getTitle())))
                        .andExpect(jsonPath("$.content", is(post.getContent())))
                        .andExpect(jsonPath("$.author", is(post.getAuthor())));

            }
        }

        @Nested
        @DisplayName("id??? ???????????? ???????????? ?????????")
        class Context_when_post_is_not_exist {

            @BeforeEach
            void setUp() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @DisplayName("???????????? ?????? ??? ????????? ????????? ?????????.")
            @Test
            void it_throw_postNotFoundException() throws Exception {
                mockMvc.perform(get("/posts/" + post.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????????")
    class Describe_post {
        PostsSaveRequestData postSaveReauestData;

        @Nested
        @DisplayName("????????? ????????? ???????????????")
        class Context_with_new_post {
            @BeforeEach
            void setUp() {
                postSaveReauestData = getPostSaveData();
            }

            @Test
            @DisplayName("???????????? ???????????? ???????????? Created??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postSaveReauestData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("????????? ??????????????? ????????? ??????")
        class Context_when_wrong_accesstoken {
            @BeforeEach
            void setUp() {
                postSaveReauestData = getPostSaveData();
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postSaveReauestData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xxx")
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_when_none_accesstoken {
            @BeforeEach
            void setUp() {
                postSaveReauestData = getPostSaveData();
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postSaveReauestData)))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????????")
    class Describe_patch {
        Posts post;
        PostsUpdateRequestData updateRequestData;

        @BeforeEach
        void setUp() throws Exception {
            post = preparePost(getPostSaveData());
            updateRequestData = PostsUpdateRequestData.builder()
                    .title(NEW_TITLE)
                    .content(NEW_CONTENT)
                    .build();
        }

        @Nested
        @DisplayName("id??? ???????????? ???????????? ???????????????")
        class Context_with_modify_post_data {

            @Test
            @DisplayName("????????? ????????? ???????????? ????????????.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("?????? ??? ?????? ??????????????????")
        class Context_with_NonExist_id {
            @BeforeEach
            void setUpRemovePost() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @DisplayName("404 NotFound??? ????????????.")
            @Test
            void it_response_with_NotFound() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("????????? ??????????????? ????????? ??????")
        class Context_when_wrong_accesstoken {

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xxx")
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_when_none_accesstoken {

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData)))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????????")
    class Describe_delete {
        Posts post;

        @BeforeEach
        void setUp() throws Exception {
            post = preparePost(getPostSaveData());
        }

        @Nested
        @DisplayName("id??? ???????????? ???????????? ????????????")
        class Context_when_exist_id_post {
            @Test
            @DisplayName("???????????? ???????????? NOCONTENT??? ????????????.")
            void it_return_Status_NOCONTENT() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("id??? ???????????? ???????????? ?????????")
        class Context_when_not_exist_id_post {
            @BeforeEach
            void setUpRemovePost() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @Test
            @DisplayName("???????????? ?????? ??? ????????? ????????? ?????????.")
            void it_throw_PostNotFoundException() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("????????? ??????????????? ????????? ??????")
        class Context_when_wrong_accesstoken {
            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_Status_NOCONTENT() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId())
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xxx")
                        )
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_when_none_accesstoken {
            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_Status_NOCONTENT() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId()))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    private PostsSaveRequestData getPostSaveData() {
        return PostsSaveRequestData.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .email(EMAIL)
                .build();
    }

    private Posts preparePost(PostsSaveRequestData saveRequestData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/posts")
                .content(objectMapper.writeValueAsString(saveRequestData))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",
                        "Bearer " + sessionResponseData.getAccessToken())
        );

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, Posts.class);
    }

    private void deletePostBeforeTest(Long id) throws Exception {
        mockMvc.perform(delete("/posts/" + id)
                .header("Authorization",
                        "Bearer " + sessionResponseData.getAccessToken()));
    }

    private List<Posts> getPostList() throws Exception {
        ResultActions actions = mockMvc.perform(get("/posts"));

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, List.class);
    }

    private UserSaveRequestData getUserSaveData() {
        return UserSaveRequestData.builder()
                .email("test@email.com")
                .name("???????????????")
                .password("password123*")
                .picture("picture.jpg")
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
