package com.kyuwon.booklog.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kyuwon.booklog.domain.posts.BaseTimeEntity;
import com.kyuwon.booklog.dto.user.UserData;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 사용자 정보를 저장하는 객체
 */
@ToString
@Getter
@NoArgsConstructor
@EnableJpaAuditing
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class User extends BaseTimeEntity {
    /**
     * 사용자 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 사용자 이름
     */
    @Column(nullable = false)
    private String name;
    /**
     * 사용자 이메일
     */
    @Column(nullable = false)
    private String email;
    /**
     * 사용자 비밀번호
     */
    @Column(nullable = false)
    private String password;
    /**
     * 사용자 프로필 사진
     */
    @Column
    private String picture;
    /**
     * 사용자 권한
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;

    /**
     * 사용자 권한 코드 키를 가져온다.
     *
     * @return 사용자 권한 키
     */
    public String getRoleKey() {
        return this.role.getKey();
    }

    @Builder
    public User(String name, String email, String password, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;
    }

    /**
     * 비밀번호를 암호화한다.
     *
     * @param password        비밀번호
     * @param passwordEncoder 비밀번호 인코더
     */
    public void encodePassword(String password,
                               PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    /**
     * 사용자 정보를 수정한다.
     *
     * @param changeData
     */
    public void changeUser(UserData changeData) {
        name = changeData.getName();
        picture = changeData.getPicture();
        password = changeData.getPassword();
    }

    /**
     * 사용자를 탈퇴 처리한다.
     */
    public void deleted() {
        deleted = true;
    }

    /**
     * 탈퇴한 사용자가 아니고 비밀번호가 일치하면 true, 그렇지 않으면 false를 리턴한다.
     *
     * @param password 사용자 비밀번호
     * @return 탈퇴한 사용자가 아니고 비밀번호 일치 true, 아니면 false
     */
    public boolean authenticate(String password) {
        return !deleted && password.equals(this.password);
    }
}
