package com.kyuwon.booklog.dto.user;

import com.kyuwon.booklog.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 사용자 생성 모델
 */
@Getter
@NoArgsConstructor
public class UserSaveRequestData {
    /**
     * 사용자 이름
     */
    @NotBlank
    private String name;
    /**
     * 사용자 이메일
     */
    @NotBlank
    @Email
    private String email;
    /**
     * 사용자 비밀번호
     */
    @NotBlank
    @Size(min = 8, max = 1024)
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
                .build();
    }
}
