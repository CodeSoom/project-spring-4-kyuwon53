package com.kyuwon.booklog.domain.posts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostsRepository")
@SpringBootTest
public class PostsRepositoryTest {
    private static final String TITLE = "테스트 게시글";
    private static final String CONTENT = "테스트 본문";
    private static final String AUTHOR = "테스트 저자";

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanUp() {
        postsRepository.deleteAll();
    }

    @Nested
    @DisplayName("게시글 저장하고 불러오기는")
    class Describe_save_posts {
        @Nested
        @DisplayName("게시글 정보를 받아 저장하고")
        class Context_has_post_info {
            @BeforeEach
            void setUp() {
                postsRepository.save(Posts.builder()
                        .title(TITLE)
                        .content(CONTENT)
                        .author(AUTHOR)
                        .build());
            }

            @Test
            @DisplayName("저장된 게시글 목록을 리턴한다.")
            void it_return_list() {
                List<Posts> postsList = postsRepository.findAll();

                Posts posts = postsList.get(0);
                assertThat(posts.getTitle()).isEqualTo(TITLE);
                assertThat(posts.getContent()).isEqualTo(CONTENT);
            }
        }
    }

    @Nested
    @DisplayName("글 등록시간은")
    class Discribe_BaseTimeEntity {
        @Nested
        @DisplayName("글이 등록되면")
        class Context_post {
            LocalDateTime now;

            @BeforeEach
            void setUp() {
                now = LocalDateTime.now();
                postsRepository.save(Posts.builder()
                        .title(TITLE)
                        .content(CONTENT)
                        .author(AUTHOR)
                        .build());

            }

            @Test
            @DisplayName("현재 시간이 저장된다.")
            void it_return_current_time() {
                List<Posts> postsList = postsRepository.findAll();

                Posts posts = postsList.get(0);

                assertThat(posts.getCreatedDate()).isAfter(now);
                assertThat(posts.getModifiedDate()).isAfter(now);
            }
        }
    }
}
