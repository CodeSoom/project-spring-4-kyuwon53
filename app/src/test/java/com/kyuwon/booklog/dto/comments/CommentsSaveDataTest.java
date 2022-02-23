package com.kyuwon.booklog.dto.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("댓글 요청 데이터는")
class CommentsSaveDataTest {
    private Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeEach
    void setValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("필수 입력 값이 들어오면")
    class Context_with_Not_blank_comment {
        Long postId = 1L;
        String content = "content";
        String email = "email@test.com";

        @Test
        @DisplayName("필수 입력 값이 저장된다.")
        public void it_return_comment() {
            CommentsSaveData commentsSaveData = CommentsSaveData.builder()
                    .postId(postId)
                    .content(content)
                    .email(email)
                    .build();

            assertThat(commentsSaveData.getPostId()).isEqualTo(postId);
            assertThat(commentsSaveData.getContent()).isEqualTo(content);
            assertThat(commentsSaveData.getEmail()).isEqualTo(email);
        }
    }
}
