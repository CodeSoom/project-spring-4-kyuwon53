package com.kyuwon.booklog.errors;

/**
 * 중복된 이메일을 발견했을 때 던집니다.
 */
public class UserEmailDuplicationException extends RuntimeException {
    /**
     * 이메일이 중복됐을 경우 이미 존재하는 이메일이라는 메세지를 보여준다.
     *
     * @param email 중복된 이메일
     */
    public UserEmailDuplicationException(String email) {
        super("이미 존재하는 이메일 입니다. email= " + email);
    }
}
