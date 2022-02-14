package com.kyuwon.booklog.dto.user;

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

@DisplayName("사용자 요청 데이터는")
class UserSaveRequestDataTest {
    private final static String NAME = "name";
    private final static String EMAIL = "email@gmail.com";
    private final static String PASSWORD = "testpassword1234*";
    private final static String PICTURE = "picture";

    private Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("필수 입력 값이 들어오면")
    class Context_with_not_Blank {

        @Test
        @DisplayName("필수 입력 값이 저장된다.")
        public void it_return_data() {
            UserSaveRequestData userSaveRequestData = new UserSaveRequestData(NAME, EMAIL, PASSWORD, PICTURE);

            assertThat(userSaveRequestData.getEmail()).isEqualTo(EMAIL);
            assertThat(userSaveRequestData.getName()).isEqualTo(NAME);
            assertThat(userSaveRequestData.getPassword()).isEqualTo(PASSWORD);
            assertThat(userSaveRequestData.getPicture()).isEqualTo(PICTURE);
        }

        @Nested
        @DisplayName("필수 입력 값에 빈 값이 들어간다면")
        class Context_without_data {
            UserSaveRequestData saveRequestData;

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("validator에 걸리고 메세지를 출력한다.")
            public void it_validation(String empty) {
                saveRequestData = UserSaveRequestData.builder()
                        .email(empty)
                        .name(empty)
                        .password(empty)
                        .picture(empty)
                        .build();

                Set<ConstraintViolation<UserSaveRequestData>> violations = validator.validate(saveRequestData);

                Iterator<ConstraintViolation<UserSaveRequestData>> iterator = violations.iterator();
                List<String> messages = new ArrayList<>();

                while (iterator.hasNext()) {
                    ConstraintViolation<UserSaveRequestData> next = iterator.next();
                    messages.add(next.getMessage());
                }

                assertThat(violations).isNotEmpty();
                assertThat(messages).contains("이름은 필수입니다.",
                        "이메일은 필수입니다.",
                        "비밀번호는 필수입니다.");
            }
        }
    }

    @Nested
    @DisplayName("이메일 형식에 맞지 않는 값이 들어온다면")
    class Context_when_wrong_email {
        private UserSaveRequestData saveRequestData;

        @DisplayName("이메일 형식이 잘못됐다는 메세지가 나온다.")
        @ParameterizedTest
        @ValueSource(strings = {"asdfga", "asf@", "@sf", "sfa.com", "@safa.com"})
        void it_return_wrong_email_pattern(String email) {
            saveRequestData = UserSaveRequestData.builder()
                    .email(email)
                    .name(NAME)
                    .password(PASSWORD)
                    .picture(PICTURE)
                    .build();
            Set<ConstraintViolation<UserSaveRequestData>> violations = validator.validate(saveRequestData);
            Iterator<ConstraintViolation<UserSaveRequestData>> iterator = violations.iterator();
            List<String> messages = new ArrayList<>();

            while (iterator.hasNext()) {
                ConstraintViolation<UserSaveRequestData> next = iterator.next();
                messages.add(next.getMessage());
            }
            assertThat(messages).contains("이메일 형식이 아닙니다.");
        }
    }

    //TODO 비밀번호 경계값 테스트
    @Nested
    @DisplayName("비밀번호 형식에 맞지 않는 값이 들어오면")
    class Context_when_password_size_min {
        private UserSaveRequestData saveRequestData;

        @DisplayName("검증에 걸리고 잘못된 값이라는 메세지가 나온다.")
        @ParameterizedTest
        @ValueSource(strings = {"123",
                "12345678",
                "abcdefgh",
                "123456789012345678901",
                "abcdefghijklmnopqrstuwxyz",
                "1234567890abcdefghkro",
                "!@#$%^&*!@#$%^&*",
                "test1234="
        })
        void it_return_wrong_password_size(String password) {
            saveRequestData = UserSaveRequestData.builder()
                    .email(EMAIL)
                    .name(NAME)
                    .password(password)
                    .picture(PICTURE)
                    .build();

            Set<ConstraintViolation<UserSaveRequestData>> violations = validator.validate(saveRequestData);
            Iterator<ConstraintViolation<UserSaveRequestData>> iterator = violations.iterator();
            List<String> messages = new ArrayList<>();

            while (iterator.hasNext()) {
                ConstraintViolation<UserSaveRequestData> next = iterator.next();
                messages.add(next.getMessage());
            }
            assertThat(messages).contains("비밀번호는 영어와 숫자,특수문자(!@#$%^&*)를 포함해서 8~20자리 이내로 입력하세요.");
        }
    }
}
