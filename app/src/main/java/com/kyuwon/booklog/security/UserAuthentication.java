package com.kyuwon.booklog.security;

import com.kyuwon.booklog.domain.user.Role;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 인증정보를 표현한다.
 */
public class UserAuthentication extends AbstractAuthenticationToken {
    @Getter
    private String accessToken;

    private Long userId;

    public UserAuthentication(Role role, String accessToken, Long userId) {
        super(authorities(role));
        this.accessToken = accessToken;
        this.userId = userId;
    }

    public UserAuthentication(Role role, String accessToken) {
        super(authorities(role));
        this.accessToken = accessToken;
    }

    /**
     * 사용자 권한을 설정하고 리턴한다.
     *
     * @param role
     * @return
     */
    private static List<GrantedAuthority> authorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(role.getKey()));
        return authorities;
    }

    /**
     * 사용자 인증 키를 리턴한다.
     *
     * @return 토큰
     */
    @Override
    public Object getCredentials() {
        return accessToken;
    }

    /**
     * 사용자 아이디를 리턴한다.
     *
     * @return 사용자 아이디
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }
}
