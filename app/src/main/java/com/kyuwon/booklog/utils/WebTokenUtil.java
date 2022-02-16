package com.kyuwon.booklog.utils;

import com.kyuwon.booklog.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * 토큰에 관한 기능을 제공한다.
 */
@Component
public class WebTokenUtil {
    private final Key key;

    public WebTokenUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * id를 인코딩된 token으로 변환해 리턴한다.
     *
     * @param id 유저 id
     * @return 인코딩된 token
     */
    public String encode(Long id) {

        return Jwts.builder()
                .claim("userId", id)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰을 디코딩해서 id값을 리턴한다.
     * 유효하지 않은 토큰이 들어올 경우 예외를 던진다.
     *
     * @param token 토큰
     * @return 사용자 id
     * @throws InvalidTokenException 유효하지 않은 토큰이 들어올 경우
     */
    public Claims decode(String token) {
        if (!checkValidToken(token)) {
            throw new InvalidTokenException(token);
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidTokenException(token);
        }
    }

    /**
     * token이 유효하면 true, 유효하지 않으면 false를 리턴한다.
     *
     * @param token 토큰
     * @return 유효하면 true, 유효하지 않으면 false
     */
    private boolean checkValidToken(String token) {
        return !(token == null || token.isBlank());
    }
}
