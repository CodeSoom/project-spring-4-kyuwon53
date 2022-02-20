package com.kyuwon.booklog.errors;

/**
 * 비밀번호가 일치하지 않을 경우
 */
public class LoginNotMatchPasswordException extends RuntimeException {
    public LoginNotMatchPasswordException(String email) {
        super("비밀번호가 일치 하지 않습니다. password: " + email);
    }
}
