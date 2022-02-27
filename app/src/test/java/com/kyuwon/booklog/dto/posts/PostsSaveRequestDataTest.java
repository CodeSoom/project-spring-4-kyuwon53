package com.kyuwon.booklog.dto.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시물 요청 데이터는")
class PostsSaveRequestDataTest {
    private Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("제목, 내용, 작성자가 빈 값이 아니면")
    class Context_with_not_blank {
        String title = "title";
        String content = "content";
        String author = "author";
        String email = "test@email.com";

        @Test
        @DisplayName("제목, 내용, 작성자가 저장된다.")
        public void it_return_post() {
            PostsSaveRequestData requestData = new PostsSaveRequestData(title, content, author,email);

            assertThat(requestData.getTitle()).isEqualTo(title);
            assertThat(requestData.getContent()).isEqualTo(content);
            assertThat(requestData.getAuthor()).isEqualTo(author);
            assertThat(requestData.getEmail()).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("제목, 내용, 작성자에 빈 값이 들어간다면")
    class Context_without_title {
        PostsSaveRequestData requestData;

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("validator이 값을 가진다.")
        public void it_validation(String empty) {
            requestData = PostsSaveRequestData.builder()
                    .title(empty)
                    .content(empty)
                    .author(empty)
                    .email(empty)
                    .build();
            Set<ConstraintViolation<PostsSaveRequestData>> violations = validator.validate(requestData);

            assertThat(violations).isNotEmpty();
        }
    }
}
