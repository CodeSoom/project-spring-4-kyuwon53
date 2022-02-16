package com.kyuwon.booklog.errors;

/**
 * 로그인에 실패했을 때 던집니다.
 */
public class LoginFailException extends RuntimeException {
    public LoginFailException(String email) {
        super("해당 이메일 로그인에 실패했습니다. email: " + email);
    }
}
