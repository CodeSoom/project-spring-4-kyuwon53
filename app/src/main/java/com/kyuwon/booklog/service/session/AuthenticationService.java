package com.kyuwon.booklog.service.session;

import com.kyuwon.booklog.utils.WebTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 인증을 처리한다.
 */
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final WebTokenUtil jwtUtil;

    public String login() {

        return jwtUtil.encode(1L);
    }
}
