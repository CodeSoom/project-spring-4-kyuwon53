package com.kyuwon.booklog.service.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("게시물 관리 ")
class PostsServiceTest {
    private static final String TITLE = "테스트 제목";
    private static final String CONTENT = "테스트 내용";
    private static final String AUTHOR = "책 저자";
    private static final String EMAIL = "test@gmail.com";
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

        @Nested
        @DisplayName("게시물을 입력받으면")
        class Context_when_post {

            @Test
            @DisplayName("저장하고 게시물을 리턴한다.")
            void it_return_post() {
                post = postsService.save(getPost("1"));

                assertThat(post.getTitle()).isEqualTo(TITLE+"1");
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

        @Nested
        @DisplayName("등록된 id가 주어진다면")
        class Context_when_existed_id {
            @BeforeEach
            void setUp() {
                post = postsService.save(getPost("1"));
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

        @Nested
        @DisplayName("등록되지 않은 id가 주어진다면")
        class Context_when_not_existed_id {
            Long id;

            @BeforeEach
            void setUp() {
                Posts post = postsService.save(getPost("1"));

                id = post.getId();

                postsService.delete(id);
            }

            @Test
            @DisplayName("해당 게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_postNotFoundException() {
                updateRequestData = PostsUpdateRequestData.builder()
                        .title(NEW_TITLE)
                        .content(NEW_CONTENT)
                        .build();

                assertThatThrownBy(() -> postsService.update(id, updateRequestData))
                        .isInstanceOf(PostsNotFoundException.class);
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
                    postsService.save(getPost("" + i));
                }
            }

            @DisplayName("게시물 전체 목록을 반환한다.")
            @Test
            void it_return_posts() {
                assertThat(postsService.getPosts()).isNotEmpty();
                assertThat(postsService.getPosts()).hasSize(postCount);
            }
        }

        @Nested
        @DisplayName("게시물이 존재하지 않는다면")
        class Context_none_posts {
            @BeforeEach
            void setUp() {
                postsRepository.deleteAll();
            }

            @DisplayName("빈 리스트를 리턴한다.")
            @Test
            void it_return_empty_list() {
                assertThat(postsService.getPosts()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("게시물 상세 조회는")
    class Describe_getPost {
        @Nested
        @DisplayName("id에 해당하는 게시물이 존재하면")
        class Context_exist_post {
            Posts post;
            Long id;

            @BeforeEach
            void setUp() {
                post = postsService.save(getPost("1"));
                id = post.getId();
            }

            @DisplayName("게시물 상세 정보를 리턴한다.")
            @Test
            void it_return_post_detail() {
                assertThat(postsService.getPost(id).getTitle()).isEqualTo(post.getTitle());
                assertThat(postsService.getPost(id).getContent()).isEqualTo(post.getContent());
                assertThat(postsService.getPost(id).getAuthor()).isEqualTo(post.getAuthor());
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 존재하지 않는다면")
        class Context_not_exist_post {
            Posts post;
            Long id;

            @BeforeEach
            void setUp() {
                post = postsService.save(getPost("1"));
                id = post.getId();
                postsService.delete(id);
            }

            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            @Test
            void it_throw_PostNotFoundException() {
                assertThatThrownBy(() -> postsService.getPost(id))
                        .isInstanceOf(PostsNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("게시물 삭제는")
    class Describe_post_delete {
        @Nested
        @DisplayName("id에 해당하는 게시물이 있다면")
        class Context_when_exist_id {
            Posts post;
            Long id;

            @BeforeEach
            void setUp() {
                post = postsService.save(getPost("1"));
                id = post.getId();
            }

            @Test
            @DisplayName("삭제하고 삭제된 게시물을 리턴한다.")
            void it_return_deleted_post() {
                Posts result = postsService.delete(id);
                assertThat(result.getTitle()).isEqualTo(post.getTitle());
            }
        }

        @Nested
        @DisplayName("id에 해당하는 게시물이 없다면")
        class Context_when_not_exist_id {
            Posts post;
            Long id;

            @BeforeEach
            void setUp() {
                post = postsService.save(getPost("1"));
                id = post.getId();
                postsService.delete(id);
            }

            @Test
            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_postNotFoundException() {
                assertThatThrownBy(() -> postsService.delete(id))
                        .isInstanceOf(PostsNotFoundException.class);
            }
        }
    }

    private PostsSaveRequestData getPost(String suffix) {
        return PostsSaveRequestData.builder()
                .title(TITLE + suffix)
                .content(CONTENT)
                .author(AUTHOR)
                .email(EMAIL)
                .build();
    }
}
