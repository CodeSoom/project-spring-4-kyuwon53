package com.kyuwon.booklog.controller.exception;

import com.kyuwon.booklog.dto.error.ErrorRespose;
import com.kyuwon.booklog.errors.InvalidTokenException;
import com.kyuwon.booklog.errors.LoginFailException;
import com.kyuwon.booklog.errors.LoginNotMatchPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 세션 HTTP 요청이 실패하는 경우 처리한다.
 */
@ResponseBody
@ControllerAdvice
public class SessionControllerErrorAdvice {
    /**
     * 로그인이 실패하는 경우에 대한 에러 응답을 리턴한다.
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorRespose handleLoginFailException() {
        return new ErrorRespose("로그인에 실패했습니다.");
    }

    /**
     * 비밀번호가 일치하지 않는 경우에 대한 에러 응답을 리턴한다.
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginNotMatchPasswordException.class)
    public ErrorRespose handleLoginNotMatchPasswordException() {
        return new ErrorRespose("비밀번호가 일치 하지 않습니다.");
    }

    /**
     * 잘못된 엑세스 토큰일 경우에 대한 에러 응답을 리턴한다.
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorRespose handleInvalidTokenException() {
        return new ErrorRespose("잘못된 엑세스 토큰입니다.");
    }
}
