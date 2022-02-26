package com.kyuwon.booklog.service.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.errors.UserEmailNotMatchesException;
import com.kyuwon.booklog.errors.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.format.DateTimeFormatter;

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
        String email;

        @BeforeEach
        void prepareUser() {
            user = preparedUser(getUser());
            email = user.getEmail();
        }

        @Nested
        @DisplayName("본인 정보를 수정하면")
        class Context_when_match_id {

            @Test
            @DisplayName("수정하고 수정된 정보를 리턴한다.")
            void it_return_updatedUser() {
                User updateUser = userService.updateUser(email, getModifyUserData(email));

                assertThat(updateUser.getName()).isEqualTo(NEW_NAME);
                assertThat(updateUser.getPicture()).isEqualTo(NEW_PICTURE);
            }
        }

        @Nested
        @DisplayName("없는 계정을 수정을 하면")
        class Context_when_none_id {

            @BeforeEach
            void setUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("사용자를 찾을 수 없다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.updateUser(email, getModifyUserData(email)))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("본인 정보가 아닐 경우")
        class Context_when_not_match_email {

            @Test
            @DisplayName("이메일이 일치하지 않는다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.updateUser(email, getModifyUserData("xx" + email)))
                        .isInstanceOf(UserEmailNotMatchesException.class);
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

            @Test
            @DisplayName("탈퇴하고 리턴한다.")
            void it_return_update_user() {
                User deleteUser = userService.deleteUser(user.getId(), email);

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
                assertThatThrownBy(() -> userService.deleteUser(user.getId(), email))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("사용자 정보와 요청자가 다를 경우")
        class Context_when_not_matches_email {

            @Test
            @DisplayName("이메일이 일치하지 않는다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.deleteUser(user.getId(), "x" + email))
                        .isInstanceOf(UserEmailNotMatchesException.class);
            }
        }
    }

    @Nested
    @DisplayName("사용자 상제 조회는")
    class Describe_detail {
        User user;
        String email;

        @BeforeEach
        void createUser() {
            user = preparedUser(getUser());
        }

        @Nested
        @DisplayName("존재하는 이메일이면")
        class Context_when_exist_email {

            @Test
            @DisplayName("사용자 정보를 리턴한다.")
            void it_return_user_data() {
                email = user.getEmail();
                User userDetail = userService.detailUser(email);

                assertThat(userDetail.getName()).isEqualTo(user.getName());
                assertThat(userDetail.getEmail()).isEqualTo(user.getEmail());
                assertThat(userDetail.getPicture()).isEqualTo(user.getPicture());
                assertThat(userDetail.getCreatedDate()
                        .format(DateTimeFormatter.ofPattern("YYYY-mm-dd HH:MM:ss")))
                        .isEqualTo(user.getCreatedDate()
                                .format(DateTimeFormatter.ofPattern("YYYY-mm-dd HH:MM:ss")));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 이메일 조회하면")
        class Context_when_not_exist_email {

            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
                email = user.getEmail();
            }

            @Test
            @DisplayName("사용자를 찾을 수 없다는 예외를 던진다.")
            void it_throw_NotFoundUserException() {
                assertThatThrownBy(() -> userService.deleteUser(user.getId(), email))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("사용자 목록 조회는")
    class Describe_list {
        @Nested
        @DisplayName("사용자가 있다면")
        class Context_when_exist_user {
            UserSaveRequestData userSaveRequestData;
            int count = 10;

            @BeforeEach
            void setUp() {
                for (int i = 0; i < count; i++) {
                    userSaveRequestData = UserSaveRequestData.builder()
                            .email(EMAIL + i)
                            .picture(PICTURE)
                            .name(NAME + i)
                            .password(PASSWORD)
                            .build();

                    preparedUser(userSaveRequestData);
                }
            }

            @Test
            @DisplayName("사용자 목록을 리턴한다.")
            void it_return_user_list() {
                assertThat(userService.userList()).hasSize(count);
            }
        }

        @Nested
        @DisplayName("사용자가 존재하지 않으면")
        class Context_not_exist_user {
            @BeforeEach
            void cleanUser() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            void it_return_empty_list() {
                assertThat(userService.userList()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("이메일 조회는 ")
    class Describe_getUserEmailById {
        @Nested
        @DisplayName("존재하는 사용자라면")
        class Context_when_exist_user {
            User user;

            @BeforeEach
            void prepareUser() {
                user = preparedUser(getUser());
            }

            @Test
            @DisplayName("id에 해당하는 email을 리턴한다.")
            void it_return_email_by_id() {
                Long id = user.getId();
                assertThat(userService.getUserEmailById(id)).isEqualTo(user.getEmail());
            }
        }

        @Nested
        @DisplayName("존재하지 않은 사용자라면")
        class Context_when_not_exist_user {
            @BeforeEach
            void cleanUp() {
                userRepository.deleteAll();
            }

            @Test
            @DisplayName("사용자를 찾을 수 없다는 예외를 던진다.")
            void it_throw_UserNotFoundException() {
                assertThatThrownBy(() -> userService.getUserEmailById(1L))
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
