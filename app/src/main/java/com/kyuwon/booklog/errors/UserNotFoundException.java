package com.kyuwon.booklog.errors;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("해당 이메일의 사용자를 찾을 수 없습니다. email: " + email);
    }

    public UserNotFoundException(Long id) {
        super("해당 아이디의 사용자를 찾을 수 없습니다. id: " + id);
    }
}
