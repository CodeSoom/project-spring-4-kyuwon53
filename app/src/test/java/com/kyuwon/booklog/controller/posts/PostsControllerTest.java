package com.kyuwon.booklog.controller.posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import com.kyuwon.booklog.service.posts.PostsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
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
    @DisplayName("GET 요청은")
    class Decribe_GET {
        @Nested
        @DisplayName("등록된 게시물이 있다면")
        class Context_exist_post {
            final int postCount = 10;

            @BeforeEach
            void setUp() {
                List<Posts> postsList = new ArrayList<>();

                for (int i = 0; i < postCount; i++) {
                    postsController.create(getPost());
                    postsList.add(getPost().toEntity());
                }
                given(postsService.getPosts()).willReturn(postsList);
            }

            @Test
            @DisplayName("전체 리스트를 리턴한다.")
            void it_return_list() throws Exception {
                mockMvc.perform(get("/posts"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(TITLE)));
            }
        }

        @Nested
        @DisplayName("등록된 게시물이 없다면")
        class Context_not_exist_post {
            private static final String EMPTY_LIST = "[]";

            @BeforeEach
            void setUp() {
                postsRepository.deleteAll();
            }

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            void it_return_empty_list() throws Exception {
                mockMvc.perform(get("/posts"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(EMPTY_LIST)));
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 있다면")
        class Context_exist_id_post {
            Long id;

            @BeforeEach
            void setUp() {
                Posts post = preparePost();
                id = post.getId();
            }

            @DisplayName("게시물을 응답한다.")
            @Test
            void it_response_post() throws Exception {
                mockMvc.perform(get("/posts/" + id))
                        .andExpect(status().isOk());

                verify(postsService).getPost(id);
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 없다면")
        class Context_when_post_is_not_exist {
            Long id;

            @BeforeEach
            void setUp() {
                Posts source = preparePost();
                id = source.getId();
                postsRepository.deleteById(id);

                given(postsService.getPost(any(Long.class)))
                        .willThrow(PostsNotFoundException.class);
            }

            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            @Test
            void it_throw_postNotFoundException() throws Exception {
                mockMvc.perform(get("/posts/" + id))
                        .andExpect(status().isNotFound());
            }
        }
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

        @Nested
        @DisplayName("id에 해당하는 게시물이 존재한다면")
        class Context_with_modify_post_data {
            String updatePostdata;
            Long id;
            PostsUpdateRequestData updateRequestData;
            Posts post;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                post = preparePost();
                id = post.getId();

                updateRequestData = PostsUpdateRequestData.builder()
                        .title(NEW_TITLE)
                        .content(NEW_CONTENT)
                        .build();

                updatePostdata = objectMapper.writeValueAsString(updateRequestData);

                given(postsService.update(id, updateRequestData))
                        .willReturn(post);
            }

            @Test
            @DisplayName("게시물 정보를 수정하고 리턴한다.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePostdata))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("DELETE 요청은")
    class Describe_delete {
        @Nested
        @DisplayName("id에 해당하는 게시물이 존재하면")
        class Context_when_exist_id_post {
            Posts post;
            Long id;

            @BeforeEach
            void setUp() {
                post = preparePost();
                id = post.getId();
            }

            @Test
            @DisplayName("게시물을 삭제하고 NOCONTENT를 응답한다.")
            void it_return_Status_NOCONTENT() throws Exception {
                mockMvc.perform(delete("/posts/" + id))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 없다면")
        class Context_when_not_exist_id_post {
            Long id;

            @BeforeEach
            void setUp() {
                Posts post = preparePost();
                id = post.getId();
                postsRepository.deleteById(id);

                given(postsService.delete(id))
                        .willThrow(PostsNotFoundException.class);
            }

            @Test
            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_PostNotFoundException() throws Exception {
                mockMvc.perform(delete("/posts/"+id))
                        .andExpect(status().isNotFound());
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

    private Posts preparePost() {
        return postsRepository.save(getPost().toEntity());
    }
}
