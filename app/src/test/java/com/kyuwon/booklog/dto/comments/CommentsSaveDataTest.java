package com.kyuwon.booklog.dto.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("댓글 요청 데이터는")
class CommentsSaveDataTest {
    private static final Long POST_ID = 1L;
    private String CONTENT = "content";
    private String EMAIL = "email@test.com";

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
        @Test
        @DisplayName("필수 입력 값이 저장된다.")
        public void it_return_comment() {
            CommentsSaveData commentsSaveData = CommentsSaveData.builder()
                    .postId(POST_ID)
                    .content(CONTENT)
                    .email(EMAIL)
                    .build();

            assertThat(commentsSaveData.getPostId()).isEqualTo(POST_ID);
            assertThat(commentsSaveData.getContent()).isEqualTo(CONTENT);
            assertThat(commentsSaveData.getEmail()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("필수 입력 값에 빈 값이 들어간다면")
    class Context_when_blank_data {
        private CommentsSaveData commentsSaveData;

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("validator에 걸리고 메시지를 출력한다.")
        void it_validation_return_message(String blank) {
            commentsSaveData = CommentsSaveData.builder()
                    .postId(null)
                    .content(blank)
                    .email(blank)
                    .build();

            Set<ConstraintViolation<CommentsSaveData>> violations = validator.validate(commentsSaveData);

            Iterator<ConstraintViolation<CommentsSaveData>> iterator = violations.iterator();
            List<String> messages = new ArrayList<>();

            while (iterator.hasNext()) {
                ConstraintViolation<CommentsSaveData> next = iterator.next();
                messages.add(next.getMessage());
            }

            assertThat(violations).isNotEmpty();
            assertThat(messages).contains(
                    "게시물 아이디는 필수입니다.",
                    "댓글 내용은 필수입니다.",
                    "이메일은 필수입니다."
            );
        }
    }

    @Nested
    @DisplayName("이메일 형식에 맞지 않는 값이 들어온다면")
    class Context_when_wrong_email {
        private CommentsSaveData commentsSaveData;

        @DisplayName("이메일 형식이 잘못됐다는 메세지가 나온다.")
        @ParameterizedTest
        @ValueSource(strings = {"asdfga", "asf@", "@sf", "sfa.com", "@safa.com"})
        void it_return_wrong_email_pattern(String email) {
            commentsSaveData = CommentsSaveData.builder()
                    .postId(POST_ID)
                    .email(email)
                    .content(CONTENT)
                    .build();

            Set<ConstraintViolation<CommentsSaveData>> violations = validator.validate(commentsSaveData);

            Iterator<ConstraintViolation<CommentsSaveData>> iterator = violations.iterator();
            List<String> messages = new ArrayList<>();

            while (iterator.hasNext()) {
                ConstraintViolation<CommentsSaveData> next = iterator.next();
                messages.add(next.getMessage());
            }

            assertThat(violations).isNotEmpty();
            assertThat(messages).contains("이메일 형식이 아닙니다.");
        }
    }
}
