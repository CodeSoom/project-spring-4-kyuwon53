package com.kyuwon.booklog.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 사용자 로그인 데이터 모델
 */
@Getter
@NoArgsConstructor
public class UserLoginData {
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @Builder
    public UserLoginData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
