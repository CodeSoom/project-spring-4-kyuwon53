package com.kyuwon.booklog.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 HTTP 요청 결과 모델
 */
@Getter
@NoArgsConstructor
public class UserResultData {
    private String email;
    private String name;
    private String picture;

    @Builder
    public UserResultData(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
}
