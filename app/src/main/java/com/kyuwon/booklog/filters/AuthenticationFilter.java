package com.kyuwon.booklog.filters;

import com.kyuwon.booklog.domain.user.Role;
import com.kyuwon.booklog.security.UserAuthentication;
import com.kyuwon.booklog.service.session.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain
    ) throws IOException, ServletException {

        String accessToken = parseAuthorizationHeaderFrom(request);

        if (!accessToken.isBlank()) {
            Long userId = authenticationService.parseToken(accessToken);
            Authentication authResult = this.getAuthenticationManager().authenticate(
                    new UserAuthentication(Role.USER, accessToken, userId)
            );
            onSuccessfulAuthentication(request, response, authResult);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult
    ) {
        authResult.setAuthenticated(true);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authResult);
    }

    private String parseAuthorizationHeaderFrom(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            return "";
        }
        return authorization.substring("Bearer ".length());
    }
}
