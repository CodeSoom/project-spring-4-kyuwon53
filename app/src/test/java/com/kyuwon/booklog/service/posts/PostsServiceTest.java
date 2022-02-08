package com.kyuwon.booklog.service.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("게시물 관리 ")
class PostsServiceTest {
    private static final String TITLE = "테스트 제목";
    private static final String CONTENT = "테스트 내용";
    private static final String AUTHOR = "테스트 작성자";
    private static final String NEW_TITLE = "새로운 제목";
    private static final String NEW_CONTENT = "새로운 내용";

    @Autowired
    private PostsService postsService;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void cleanUp() {
        postsRepository.deleteAll();
    }

    @Nested
    @DisplayName("게시물 저장은")
    class Discribe_save {
        Posts post;
        PostsSaveRequestData postsSaveRequestData;

        @Nested
        @DisplayName("게시물을 입력받으면")
        class Context_when_post {
            @BeforeEach
            void setUp() {
                postsSaveRequestData = PostsSaveRequestData.builder()
                        .title(TITLE)
                        .content(CONTENT)
                        .author(AUTHOR)
                        .build();
            }

            @Test
            @DisplayName("저장하고 게시물을 리턴한다.")
            void it_return_post() {
                post = postsService.save(postsSaveRequestData);

                assertThat(post.getTitle()).isEqualTo(TITLE);
                assertThat(post.getContent()).isEqualTo(CONTENT);
                assertThat(post.getAuthor()).isEqualTo(AUTHOR);
            }
        }
    }

    @Nested
    @DisplayName("게시물 수정은")
    class Discribe_update {
        Posts post;
        PostsUpdateRequestData updateRequestData;
        PostsSaveRequestData saveRequestData;

        @Nested
        @DisplayName("등록된 id가 주어진다면")
        class Context_when_existed_id {
            @BeforeEach
            void setUp() {
                saveRequestData = PostsSaveRequestData.builder()
                        .title(TITLE)
                        .content(CONTENT)
                        .author(AUTHOR)
                        .build();

                post = postsService.save(saveRequestData);
            }

            @Test
            @DisplayName("id에 해당하는 게시물 정보를 수정하고 리턴한다.")
            void it_update_post_return() {
                updateRequestData = PostsUpdateRequestData.builder()
                        .title(NEW_TITLE)
                        .content(NEW_CONTENT)
                        .build();

                Long id = post.getId();

                post = postsService.update(id, updateRequestData);

                assertThat(post.getTitle()).isEqualTo(NEW_TITLE);
                assertThat(post.getContent()).isEqualTo(NEW_CONTENT);
            }
        }
    }

    @Nested
    @DisplayName("게시물 목록 조회는")
    class Discribe_getPosts {
        @Nested
        @DisplayName("게시물이 존재한다면")
        class Context_exist_posts {
            final int postCount = 10;

            @BeforeEach
            void setUp() {
                for (int i = 0; i < postCount; i++) {
                    postsService.save(getPost(""+i));
                }
            }

            @DisplayName("게시물 전체 목록을 반환한다.")
            void it_return_posts() {
                assertThat(postsService.getPosts()).isNotEmpty();
                assertThat(postsService.getPosts()).hasSize(postCount);
            }
        }
    }

    private PostsSaveRequestData getPost(String suffix) {
        return PostsSaveRequestData.builder()
                .title(TITLE + suffix)
                .content(CONTENT)
                .author(AUTHOR)
                .build();
    }
}
