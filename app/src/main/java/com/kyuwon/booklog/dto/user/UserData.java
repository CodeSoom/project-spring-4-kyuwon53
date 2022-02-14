package com.kyuwon.booklog.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 데이터 모델
 */
@Getter
@NoArgsConstructor
public class UserData {
    private String email;
    private String name;
    private String picture;

    @Builder
    public UserData(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
}
