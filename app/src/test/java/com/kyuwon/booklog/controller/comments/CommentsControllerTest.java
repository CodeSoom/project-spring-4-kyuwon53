package com.kyuwon.booklog.controller.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.comments.CommentsData;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
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
@DisplayName("댓글 HTTP 요청")
class CommentsControllerTest {
    private static final String EMAIL = "test@test.com";
    private static final String COMMENT = "댓글 테스트 내용";
    private static final String NEW_COMMENT = "새로운 댓글 테스트 내용";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostsService postsService;

    @Autowired
    private CommentService commentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Posts post;

    @BeforeEach
    public void setPost() {
        post = postsService.save(getPost());
    }

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("댓글 등록 요청")
    class Describe_post {
        CommentsSaveData commentsSaveData;

        @Nested
        @DisplayName("댓글 정보가 주어지면")
        class Context_with_comment {
            @BeforeEach
            void setUp() {
                commentsSaveData = getComment(post.getId());
            }

            @Test
            @DisplayName("댓글을 저장하고 상태코드 Created를 응답한다.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsSaveData)))
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회 요청")
    class Describe_Get {
        @Nested
        @DisplayName("게시물이 존재하고 댓글이 존재하면")
        class Context_when_exist_post_comments {
            private static final int COUNT = 10;

            @BeforeEach
            void setComment() throws Exception {
                for (int i = 0; i < COUNT; i++) {
                    prepareComment(getComment(post.getId()));
                }
            }

            @Test
            @DisplayName("댓글 목록을 리턴하고 상태 isOk를 응답한다.")
            void it_response_isOk() throws Exception {
                mockMvc.perform(get("/comments/" + post.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(COMMENT)))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("댓글 수정 요청")
    class Describe_patch {
        Comments comments;
        CommentsData commentsUpdateData;

        @BeforeEach
        void setComments() throws Exception {
            comments = prepareComment(getComment(post.getId()));

        }

        @Nested
        @DisplayName("게시물이 존재하고 댓글 작성자와 수정 요청자가 일치할 경우")
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
            @DisplayName("댓글을 수정하고 isOk 응답한다.")
            void it_update_comment_return() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content", is(commentsUpdateData.getComment())));
            }
        }

        @Nested
        @DisplayName("해당 게시물이 존재하지 않을 경우")
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
            @DisplayName("NotFound를 응답한다.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData)))
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("댓글 작성자가 아닌 사용자가 수정 요청시")
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
            @DisplayName("BadRequest를 응답한다.")
            void it_response_BadRequest() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsNotAuthor)))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("해당 댓글이 없다면")
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
            @DisplayName("NotFound를 응답한다.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(patch("/comments/" + comments.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentsUpdateData)))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("댓글 삭제 요청")
    class Describe_delete_comment {
        private Comments comment;
        private String requestEmail;
        private Long commentId;

        @BeforeEach
        void setUp() throws Exception {
            comment = prepareComment(getComment(post.getId()));

            requestEmail = comment.getEmail();
            commentId = comment.getId();
        }

        @Nested
        @DisplayName("게시물이 존재하고 작성자가 일치할 경우")
        class Context_when_exist_post_matches_email {
            @Test
            @DisplayName("isNoContent 상태를 리턴한다.")
            void it_response_isOk() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail))
                        .andDo(print())
                        .andExpect(status().isNoContent());

            }
        }

        @Nested
        @DisplayName("해당 게시물이 존재하지 않을 경우")
        class Context_when_not_exist_post {

            @BeforeEach
            void deletePost() {
                postsService.delete(post.getId());
            }

            @Test
            @DisplayName("NotFound를 응답한다.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail))
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("댓글 작성자가 아닌 사용자가 삭제 요청시")
        class Context_when_not_author {

            @Test
            @DisplayName("BadRequest를 응답한다.")
            void it_response_BadRequest() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("x" + requestEmail))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("해당 댓글이 없다면")
        class Context_When_no_comment {
            @BeforeEach
            void deleteComment() {
                commentService.delete(commentId, requestEmail);
            }

            @Test
            @DisplayName("NotFound를 응답한다.")
            void it_response_NotFound() throws Exception {
                mockMvc.perform(delete("/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestEmail))
                        .andExpect(status().isNotFound());
            }
        }
    }

    private Comments prepareComment(CommentsSaveData commentsSaveData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/comments")
                .content(objectMapper.writeValueAsString(commentsSaveData))
                .contentType(MediaType.APPLICATION_JSON));

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
                .title("테스트 제목")
                .content("게시물 내용")
                .author("AUTHOR")
                .build();
    }
}
