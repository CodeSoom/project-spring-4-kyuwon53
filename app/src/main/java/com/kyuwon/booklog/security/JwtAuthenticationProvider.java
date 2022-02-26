package com.kyuwon.booklog.security;

import com.kyuwon.booklog.domain.user.Role;
import com.kyuwon.booklog.service.session.AuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAuthentication anonymousAuthentication = (UserAuthentication) authentication;
        String accessToken = anonymousAuthentication.getAccessToken();

        Long userId = authenticationService.parseToken(accessToken);

        return new UserAuthentication(Role.USER, accessToken, userId);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthentication.class);
    }
}
