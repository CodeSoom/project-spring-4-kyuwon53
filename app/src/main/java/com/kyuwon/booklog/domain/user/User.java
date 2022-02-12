package com.kyuwon.booklog.domain.user;

import com.kyuwon.booklog.domain.posts.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 사용자 정보를 저장하는 객체
 */
@Getter
@NoArgsConstructor
@EnableJpaAuditing
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

    /**
     * 사용자 권한 코드 키를 가져온다.
     *
     * @return 사용자 권한 키
     */
    public String getRoleKey() {
        return this.role.getKey();
    }
}
