package com.kyuwon.booklog.controller.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.comments.CommentsData;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.session.SessionResponseData;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.service.comments.CommentService;
import com.kyuwon.booklog.service.posts.PostsService;
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
@DisplayName("?????? HTTP ??????")
class CommentsControllerTest {
    private static final String EMAIL = "test@test.com";
    private static final String COMMENT = "?????? ????????? ??????";
    private static final String NEW_COMMENT = "????????? ?????? ????????? ??????";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostsService postsService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Posts post;
    private User user;
    private SessionResponseData sessionResponseData;

    @BeforeEach
    public void setPost() {
        post = postsService.save(getPost());
    }

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
    @DisplayName("?????? ?????? ??????")
    class Describe_post {
        CommentsSaveData commentsSaveData;

        @Nested
        @DisplayName("?????? ????????? ????????????")
        class Context_with_comment {
            @BeforeEach
            void setUp() {
                commentsSaveData = getComment(post.getId());
            }

            @Test
            @DisplayName("????????? ???????????? ???????????? Created??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsSaveData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("????????? ??????????????? ????????? ??????")
        class Context_with_wrong_accessToken {
            @BeforeEach
            void setUp() {
                commentsSaveData = getComment(post.getId());
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsSaveData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xx")
                        )
                        .andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_with_none_accessToken {
            @BeforeEach
            void setUp() {
                commentsSaveData = getComment(post.getId());
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsSaveData)))
                        .andExpect(status().isUnauthorized())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ??????")
    class Describe_Get {
        @Nested
        @DisplayName("???????????? ???????????? ????????? ????????????")
        class Context_when_exist_post_comments {
            private static final int COUNT = 10;

            @BeforeEach
            void setComment() throws Exception {
                for (int i = 0; i < COUNT; i++) {
                    prepareComment(getComment(post.getId()), sessionResponseData.getAccessToken());
                }
            }

            @Test
            @DisplayName("?????? ????????? ???????????? ?????? isOk??? ????????????.")
            void it_response_isOk() throws Exception {
                mockMvc.perform(get("/comments/" + post.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(COMMENT)))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class Describe_patch {
        Comments comments;
        CommentsData commentsUpdateData;

        @BeforeEach
        void setComments() throws Exception {
            comments = prepareComment(getComment(post.getId()), sessionResponseData.getAccessToken());

        }

        @Nested
        @DisplayName("???????????? ???????????? ?????? ???????????? ?????? ???????????? ????????? ??????")
        class Context_when_exist_post_matches_email {
            @BeforeEach
            void setUp() {
                commentsUpdateData = CommentsData.builder()
                        .id(comments.getId())
                        .email(comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("????????? ???????????? isOk ????????????.")
            void it_update_comment_return() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content", is(commentsUpdateData.getComment())));
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ???????????? ?????? ??????")
        class Context_when_not_exist_post {

            @BeforeEach
            void deletePost() {
                postsService.delete(post.getId());
            }

            @BeforeEach
            void setUp() {
                commentsUpdateData = CommentsData.builder()
                        .id(comments.getId())
                        .email(comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("NotFound??? ????????????.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ?????? ???????????? ?????? ?????????")
        class Context_when_not_author {
            private CommentsData commentsNotAuthor;

            @BeforeEach
            void setWrongData() {
                commentsNotAuthor = CommentsData.builder()
                        .id(comments.getId())
                        .email("xx" + comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("BadRequest??? ????????????.")
            void it_response_BadRequest() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsNotAuthor))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("?????? ????????? ?????????")
        class Context_When_no_comment {
            @BeforeEach
            void deleteComment() {
                commentService.delete(comments.getId(), comments.getEmail());
            }

            @BeforeEach
            void setUp() {
                commentsUpdateData = CommentsData.builder()
                        .id(comments.getId())
                        .email(comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("NotFound??? ????????????.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("????????? ??????????????? ????????? ??????")
        class Context_when_wrong_accesstoken {
            @BeforeEach
            void setUp() {
                commentsUpdateData = CommentsData.builder()
                        .id(comments.getId())
                        .email(comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_update_comment_return() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData))
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "x")
                        )
                        .andDo(print())
                        .andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_when_none_accesstoken {
            @BeforeEach
            void setUp() {
                commentsUpdateData = CommentsData.builder()
                        .id(comments.getId())
                        .email(comments.getEmail())
                        .comment(NEW_COMMENT)
                        .build();
            }

            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_update_comment_return() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData)))
                        .andDo(print())
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class Describe_delete_comment {
        private Comments comment;
        private String requestEmail;
        private Long commentId;

        @BeforeEach
        void setUp() throws Exception {
            comment = prepareComment(getComment(post.getId()), sessionResponseData.getAccessToken());

            requestEmail = comment.getEmail();
            commentId = comment.getId();
        }

        @Nested
        @DisplayName("???????????? ???????????? ???????????? ????????? ??????")
        class Context_when_exist_post_matches_email {
            @Test
            @DisplayName("isNoContent ????????? ????????????.")
            void it_response_isOk() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail)
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isNoContent());

            }
        }

        @Nested
        @DisplayName("?????? ???????????? ???????????? ?????? ??????")
        class Context_when_not_exist_post {

            @BeforeEach
            void deletePost() {
                postsService.delete(post.getId());
            }

            @Test
            @DisplayName("NotFound??? ????????????.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail)
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ?????? ???????????? ?????? ?????????")
        class Context_when_not_author {

            @Test
            @DisplayName("BadRequest??? ????????????.")
            void it_response_BadRequest() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("x" + requestEmail)
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken())
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("?????? ????????? ?????????")
        class Context_When_no_comment {
            @BeforeEach
            void deleteComment() {
                commentService.delete(commentId, requestEmail);
            }

            @Test
            @DisplayName("NotFound??? ????????????.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail)
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
            void it_response_isOk() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail)
                                .header("Authorization",
                                        "Bearer " + sessionResponseData.getAccessToken() + "xx")
                        )
                        .andDo(print())
                        .andExpect(status().isUnauthorized());

            }
        }

        @Nested
        @DisplayName("??????????????? ?????? ??????")
        class Context_when_none_accesstoken {
            @Test
            @DisplayName("isUnauthorized??? ????????????.")
            void it_response_isOk() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail))
                        .andDo(print())
                        .andExpect(status().isUnauthorized());

            }
        }
    }

    private Comments prepareComment(CommentsSaveData commentsSaveData, String accessToken) throws Exception {
        ResultActions actions = mockMvc.perform(post("/comments")
                .content(objectMapper.writeValueAsString(commentsSaveData))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",
                        "Bearer " + accessToken)

        );

        MvcResult mvcResult = actions.andReturn();
        ;
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, Comments.class);
    }

    private CommentsSaveData getComment(Long id) {
        return CommentsSaveData.builder()
                .email(EMAIL)
                .content(COMMENT)
                .postId(id)
                .build();
    }

    private PostsSaveRequestData getPost() {
        return PostsSaveRequestData.builder()
                .title("????????? ??????")
                .content("????????? ??????")
                .author("AUTHOR")
                .email("test@email.com")
                .build();
    }

    private UserSaveRequestData getUserSaveData() {
        return UserSaveRequestData.builder()
                .email(EMAIL)
                .name("??????")
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
