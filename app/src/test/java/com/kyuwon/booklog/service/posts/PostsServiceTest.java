package com.kyuwon.booklog.service.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
}
