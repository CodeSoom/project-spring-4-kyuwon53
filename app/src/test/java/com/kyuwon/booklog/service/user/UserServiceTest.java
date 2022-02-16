package com.kyuwon.booklog.service.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("사용자 관리")
class UserServiceTest {
    private static final String EMAIL = "test@gmail.com";
    private static final String NAME = "테스트이름";
    private static final String PASSWORD = "testpassword";
    private static final String PICTURE = "테스트사진";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("회원가입은")
    class Discribe_signup {
        User user;
        UserSaveRequestData userSaveRequestData;

        @Nested
        @DisplayName("사용자 정보를 입력받으면")
        class Context_when_user_data {
            @BeforeEach
            void setUp() {
                userSaveRequestData = UserSaveRequestData.builder()
                        .email(EMAIL)
                        .name(NAME)
                        .password(PASSWORD)
                        .picture(PICTURE)
                        .build();
            }

            @Test
            @DisplayName("저장하고 사용자 정보를 리턴한다.")
            void it_return_user() {
                user = userService.signUp(userSaveRequestData);

                assertThat(user.getEmail()).isEqualTo(EMAIL);
                assertThat(user.getName()).isEqualTo(NAME);
                assertThat(user.getPicture()).isEqualTo(PICTURE);
            }
        }
    }
}
