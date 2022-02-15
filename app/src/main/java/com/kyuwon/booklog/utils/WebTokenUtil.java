package com.kyuwon.booklog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

    public Claims decode(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
