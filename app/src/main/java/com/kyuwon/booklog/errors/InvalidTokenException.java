package com.kyuwon.booklog.errors;

/**
 * 유효하지 않은 토큰이 들어올 때 던집니다.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("유효하지 않은 토큰 입니다. token: " + token);
    }
}
