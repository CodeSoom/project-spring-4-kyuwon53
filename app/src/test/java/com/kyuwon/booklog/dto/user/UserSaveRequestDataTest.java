package com.kyuwon.booklog.dto.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
    private final static String PASSWORD = "testpassword1234";
    private final static String PICTURE = "picture";

    private final static String WRONG_EMAIL = "emailgmail.com";

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
            final String[] emptyValues = new String[]{null, "", " "};
            UserSaveRequestData saveRequestData;

            @BeforeEach
            void setUp() {
                saveRequestData = UserSaveRequestData.builder()
                        .email(emptyValues[0])
                        .name(emptyValues[1])
                        .password(emptyValues[2])
                        .picture(emptyValues[0])
                        .build();
            }

            @Test
            @DisplayName("validator에 걸리고 메세지를 출력한다.")
            public void it_validation() {
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
                        "비밀번호는 영어와 숫자를 포함해서 8~20자리 이내로 입력하세요.");
            }
        }
    }

    @Nested
    @DisplayName("이메일 형식에 맞지 않는 값이 들어온다면")
    class Context_when_wrong_email {
        private UserSaveRequestData saveRequestData;

        @BeforeEach
        void setUp() {
            saveRequestData = UserSaveRequestData.builder()
                    .email(WRONG_EMAIL)
                    .name(NAME)
                    .password(PASSWORD)
                    .picture(PICTURE)
                    .build();
        }

        @Test
        @DisplayName("이메일 형식이 잘못됐다는 메세지가 나온다.")
        void it_return_wrong_email_pattern() {
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
}
