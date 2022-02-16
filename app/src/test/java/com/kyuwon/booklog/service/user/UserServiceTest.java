package com.kyuwon.booklog.service.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.errors.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("사용자 관리")
class UserServiceTest {
    private static final String EMAIL = "test@gmail.com";
    private static final String NAME = "테스트이름";
    private static final String PASSWORD = "*testpassword123";
    private static final String PICTURE = "테스트사진";

    private static final String NEW_NAME = "NEW테스트이름";
    private static final String NEW_PASSWORD = "*newpassword123";
    private static final String NEW_PICTURE = "NEW테스트사진";

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

    @Nested
    @DisplayName("회원 정보 수정은")
    class Discribe_update {
        User user;
        UserData userModifyData;
        String email;

        @BeforeEach
        void prepareUser() {
            user = preparedUser(getUser());
            email = user.getEmail();
        }

        @Nested
        @DisplayName("본인 정보를 수정하면")
        class Context_when_match_id {

            @BeforeEach
            void setUp() {
                userModifyData = getModifyUserData(email);
            }

            @Test
            @DisplayName("수정하고 수정된 정보를 리턴한다.")
            void it_return_update_user() {
                User updateUser = userService.updateUser(email, userModifyData);

                assertThat(updateUser.getName()).isEqualTo(NEW_NAME);
                assertThat(updateUser.getPicture()).isEqualTo(NEW_PICTURE);
            }
        }

        @Nested
        @DisplayName("잘못된 이메일로 수정을 하면")
        class Context_when_not_match_id {

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();

                userModifyData = getModifyUserData(email);
            }

            @Test
            @DisplayName("사용자를 찾을 수 없다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.updateUser(email, userModifyData))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("사용자 탈퇴는")
    class Describe_delete {
        User user;
        String email;

        @BeforeEach
        void prepareUser() {
            user = preparedUser(getUser());
            email = user.getEmail();
        }

        @Nested
        @DisplayName("존재하는 이메일로 탈퇴를 하면")
        class Context_when_exist_email {

            @BeforeEach
            void setUp() {
            }

            @Test
            @DisplayName("탈퇴하고 리턴한다.")
            void it_return_update_user() {
                User deleteUser = userService.deleteUser(email);

                assertThat(deleteUser.getEmail()).isEqualTo(email);
                assertThat(deleteUser.getDeleted()).isTrue();
            }
        }

        @Nested
        @DisplayName("존재하지 않는 이메일을 탈퇴하면")
        class Context_when_not_exist_email {

            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("사용자를 찾을 수 없다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.deleteUser(email))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }

    private UserData getModifyUserData(String email) {
        return UserData.builder()
                .email(email)
                .name(NEW_NAME)
                .password(NEW_PASSWORD)
                .picture(NEW_PICTURE)
                .build();
    }

    private UserSaveRequestData getUser() {
        return UserSaveRequestData.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .picture(PICTURE)
                .build();
    }

    private User preparedUser(UserSaveRequestData saveRequestData) {
        return userService.signUp(saveRequestData);
    }
}
