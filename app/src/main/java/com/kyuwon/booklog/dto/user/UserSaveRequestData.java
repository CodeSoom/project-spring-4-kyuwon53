package com.kyuwon.booklog.dto.user;

import com.kyuwon.booklog.domain.user.Role;
import com.kyuwon.booklog.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 사용자 생성 모델
 */
@Getter
@NoArgsConstructor
public class UserSaveRequestData {
    /**
     * 사용자 이름
     */
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    /**
     * 사용자 이메일
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    /**
     * 사용자 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}",
            message = "비밀번호는 영어와 숫자,특수문자(!@#$%^&*)를 포함해서 8~20자리 이내로 입력하세요.")
    private String password;
    /**
     * 사용자 프로필 사진
     */
    private String picture;

    @Builder
    public UserSaveRequestData(String name, String email, String password, String picture) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .picture(picture)
                //TODO 권한 부여 바꾸기
                .role(Role.USER)
                .build();
    }
}
