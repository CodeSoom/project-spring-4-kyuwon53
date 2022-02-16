package com.kyuwon.booklog.dto.user;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("사용자 로그인 데이터")
class UserLoginDataTest {
    private Validator validator;
    private static ValidatorFactory validatorFactory;
    private UserLoginData userLoginData;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Nested
    @DisplayName("필수 입력 값이 들어오면")
    class Context_with_not_Blank {
        private static final String EMAIL = "test@email.com";
        private static final String PASSWORD = "password123*";

        @Test
        @DisplayName("저장된다.")
        void it_return_data() {
            userLoginData = UserLoginData.builder()
                    .email(EMAIL)
                    .password(PASSWORD)
                    .build();

            assertThat(userLoginData.getEmail()).isEqualTo(EMAIL);
            assertThat(userLoginData.getPassword()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("필수 입력 값에 빈 값이 들어온다면")
    class Context_when_blank {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("validator에 걸리고 메세지를 출력한다.")
        void it_validaton(String empty) {
            userLoginData = UserLoginData.builder()
                    .email(empty)
                    .password(empty)
                    .build();

            Set<ConstraintViolation<UserLoginData>> violations = validator.validate(userLoginData);

            Iterator<ConstraintViolation<UserLoginData>> iterator = violations.iterator();
            List<String> messages = new ArrayList<>();

            while (iterator.hasNext()) {
                ConstraintViolation<UserLoginData> userLoginDataConstraintViolation = iterator.next();
                messages.add(userLoginDataConstraintViolation.getMessage());
            }

            assertThat(violations).isNotEmpty();
            assertThat(messages).contains("이메일은 필수 항목입니다."
                    , "비밀번호는 필수 항목입니다.");
        }
    }
}
