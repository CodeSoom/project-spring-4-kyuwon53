package com.kyuwon.booklog.controller.posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import com.kyuwon.booklog.service.posts.PostsService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("상품 등록 컨트롤러")
class PostsControllerTest {
    private static final String TITLE = "테스트 제목";
    private static final String CONTENT = "테스트 내용";
    private static final String AUTHOR = "테스트 저자";
    private static final String NEW_TITLE = "새로운 제목";
    private static final String NEW_CONTENT = "새로운 내용";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostsController postsController;

    @MockBean
    private PostsService postsService;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @AfterEach
    public void clean() {
        postsRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST 요청은")
    class Describe_post {
        String postSaveReauestData;

        @Nested
        @DisplayName("게시물 정보가 주어진다면")
        class Context_with_new_post {
            @BeforeEach
            void setUp() throws JsonProcessingException {
                postSaveReauestData = objectMapper.writeValueAsString(getPost());
                given(postsController.create(getPost()))
                        .willReturn(getPost().toEntity());
            }

            @Test
            @DisplayName("게시물을 저장하고 상태코드 Created를 응답한다.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postSaveReauestData))
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("PATCH 요청은")
    class Describe_patch {
        String post;
        Long id;
        PostsUpdateRequestData updateRequestData;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            updateRequestData = PostsUpdateRequestData.builder()
                    .title(NEW_TITLE)
                    .content(NEW_CONTENT)
                    .build();

            post = objectMapper.writeValueAsString(updateRequestData);
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 존재한다면")
        class Context_with_modify_post_data {
            @Test
            @DisplayName("게시물 정보를 수정하고 리턴한다.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(post))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }
    }

    private PostsSaveRequestData getPost() {
        return PostsSaveRequestData.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build();
    }
}
