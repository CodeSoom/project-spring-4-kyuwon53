package com.kyuwon.booklog.errors;

public class UserEmailNotMatchesException extends RuntimeException {
    public UserEmailNotMatchesException(String email) {
        super("요청하신 이메일이 일치하지 않습니다." + email);
    }
}
