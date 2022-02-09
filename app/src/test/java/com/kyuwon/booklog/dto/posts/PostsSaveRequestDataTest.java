package com.kyuwon.booklog.dto.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        @Test
        @DisplayName("제목, 내용, 작성자가 저장된다.")
        public void it_return_post() {
            PostsSaveRequestData requestData = new PostsSaveRequestData(title, content, author);

            assertThat(requestData.getTitle()).isEqualTo(title);
            assertThat(requestData.getContent()).isEqualTo(content);
            assertThat(requestData.getAuthor()).isEqualTo(author);
        }
    }

    @Nested
    @DisplayName("제목, 내용, 작성자에 빈 값이 들어간다면")
    class Context_without_title {
        final String[] emptyValue = new String[]{null, "", "  "};
        PostsSaveRequestData requestData;

        @BeforeEach
        void setUp() {
            requestData = PostsSaveRequestData.builder()
                    .title(emptyValue[0])
                    .content(emptyValue[1])
                    .author(emptyValue[2])
                    .build();
        }

        @Test
        @DisplayName("validator이 값을 가진다.")
        public void it_validation() {
            Set<ConstraintViolation<PostsSaveRequestData>> violations = validator.validate(requestData);

            assertThat(violations).isNotEmpty();
        }
    }
}
