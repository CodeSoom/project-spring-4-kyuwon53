package com.kyuwon.booklog.controller.posts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
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
    private WebApplicationContext wac;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("게시물 목록 조회 요청은")
    class Decribe_GET_List {
        @Nested
        @DisplayName("등록된 게시물이 있다면")
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
            @DisplayName("빈 리스트를 리턴한다.")
            void it_return_empty_list() throws Exception {
                mockMvc.perform(get("/posts"))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(EMPTY_LIST)));
            }
        }
    }

    @Nested
    @DisplayName("게시물 상세 조회 요청은")
    class Decribe_GET {
        private Posts post;

        @BeforeEach
        void setUp() throws Exception {
            post = preparePost(getPostSaveData());
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 있다면")
        class Context_exist_id_post {
            @DisplayName("게시물 상세와 200 ok HTTP 상태코드를 응답한다.")
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
        @DisplayName("id에 해당하는 게시물이 없다면")
        class Context_when_post_is_not_exist {

            @BeforeEach
            void setUp() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            @Test
            void it_throw_postNotFoundException() throws Exception {
                mockMvc.perform(get("/posts/" + post.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("게시물 등록 요청은")
    class Describe_post {
        PostsSaveRequestData postSaveReauestData;

        @Nested
        @DisplayName("게시물 정보가 주어진다면")
        class Context_with_new_post {
            @BeforeEach
            void setUp() {
                postSaveReauestData = getPostSaveData();
            }

            @Test
            @DisplayName("게시물을 저장하고 상태코드 Created를 응답한다.")
            void it_return_status_created() throws Exception {
                mockMvc.perform(post("/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postSaveReauestData)))
                        .andExpect(status().isCreated())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 수정 요청은")
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
        @DisplayName("id에 해당하는 게시물이 존재한다면")
        class Context_with_modify_post_data {

            @Test
            @DisplayName("게시물 정보를 수정하고 리턴한다.")
            void it_update_return_post() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData)))
                        .andExpect(status().isOk())
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("찾을 수 없는 게시물이라면")
        class Context_with_NonExist_id {
            @BeforeEach
            void setUpRemovePost() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @DisplayName("404 NotFound로 응답한다.")
            @Test
            void it_response_with_NotFound() throws Exception {
                mockMvc.perform(patch("/posts/" + post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestData)))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("게시물 삭제 요청은")
    class Describe_delete {
        Posts post;

        @BeforeEach
        void setUp() throws Exception {
            post = preparePost(getPostSaveData());
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 존재하면")
        class Context_when_exist_id_post {
            @Test
            @DisplayName("게시물을 삭제하고 NOCONTENT를 응답한다.")
            void it_return_Status_NOCONTENT() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId()))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 없다면")
        class Context_when_not_exist_id_post {
            @BeforeEach
            void setUpRemovePost() throws Exception {
                deletePostBeforeTest(post.getId());
            }

            @Test
            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_PostNotFoundException() throws Exception {
                mockMvc.perform(delete("/posts/" + post.getId()))
                        .andExpect(status().isNotFound());
            }
        }
    }

    private PostsSaveRequestData getPostSaveData() {
        return PostsSaveRequestData.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build();
    }

    private Posts preparePost(PostsSaveRequestData saveRequestData) throws Exception {
        ResultActions actions = mockMvc.perform(post("/posts")
                .content(objectMapper.writeValueAsString(saveRequestData))
                .contentType(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, Posts.class);
    }

    private void deletePostBeforeTest(Long id) throws Exception {
        mockMvc.perform(delete("/posts/" + id));
    }

    private List<Posts> getPostList() throws Exception {
        ResultActions actions = mockMvc.perform(get("/posts"));

        MvcResult mvcResult = actions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        return objectMapper.readValue(content, List.class);
    }
}
