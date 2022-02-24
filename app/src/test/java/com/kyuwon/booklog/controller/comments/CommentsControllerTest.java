package com.kyuwon.booklog.controller.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("댓글 HTTP 요청")
class CommentsControllerTest {
    private static final String EMAIL = "test@test.com";
    private static final String COMMENT = "댓글 테스트 내용";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostsService postsService;

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
