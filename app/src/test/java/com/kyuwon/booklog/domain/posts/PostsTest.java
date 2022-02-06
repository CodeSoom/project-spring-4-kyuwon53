package com.kyuwon.booklog.domain.posts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시물 모델")
class PostsTest {
    private static final String TITLE = "테스트 제목";
    private static final String CONTENT = "테스트 내용";
    private static final String AUTHOR = "테스트 작성자";
    private static final String NEW_TITLE = "새로운 제목";
    private static final String NEW_CONTENT = "새로운 내용";

    @Nested
    @DisplayName("게시물 수정시")
    class Describe_update {
        Posts posts;

        @BeforeEach
        void setUp() {
            posts = Posts.builder()
                    .title(TITLE)
                    .content(CONTENT)
                    .author(AUTHOR)
                    .build();
        }

        @Nested
        @DisplayName("제목,내용이 주어지면")
        class Context_with_title_content {
            @BeforeEach
            void update() {
              posts.update(NEW_TITLE, NEW_CONTENT);
            }

            @Test
            @DisplayName("변경된 제목, 내용을 리턴한다.")
            void it_return_change() {
                assertThat(posts.getTitle()).isEqualTo(NEW_TITLE);
                assertThat(posts.getContent()).isEqualTo(NEW_CONTENT);
            }
        }
    }
}
