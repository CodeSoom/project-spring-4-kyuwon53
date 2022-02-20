package com.kyuwon.booklog.dto.user;

import com.kyuwon.booklog.domain.user.User;
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
    private String password;

    @Builder
    public UserData(String email, String name, String picture, String password) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.password = password;
    }

    public static UserData of(User user) {
        return UserData.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .picture(user.getPicture())
                .build();
    }
}
