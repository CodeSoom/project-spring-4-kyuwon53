package com.kyuwon.booklog.service.session;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.errors.LoginFailException;
import com.kyuwon.booklog.errors.LoginNotMatchPasswordException;
import com.kyuwon.booklog.utils.WebTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 인증을 처리한다.
 */
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final WebTokenUtil webTokenUtil;
    private final UserRepository userRepository;

    /**
     * 사용자 이메일이 존재한다면 token을 리턴하고 존재하지 않다면 로그인이 실패했다는 예외를 던진다.
     *
     * @param userLoginData 사용자 로그인 정보
     * @return 인증 토큰
     * @throws LoginFailException 로그인 이메일을 찾을 수 없는 경우
     */
    public String login(UserLoginData userLoginData) {
        String userEmail = userLoginData.getEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new LoginFailException(userEmail));

        if (!user.authenticate(userLoginData.getPassword())) {
            throw new LoginNotMatchPasswordException(user.getEmail());
        }

        return webTokenUtil.encode(user.getId());
    }

    /**
     * 인증 토큰을 받아 id(회원 식별자)를 리턴한다.
     *
     * @param accessToken 인증토큰
     * @return 회원 id(식별자)
     */
    public Long parseToken(String accessToken) {
        Claims claims = webTokenUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
