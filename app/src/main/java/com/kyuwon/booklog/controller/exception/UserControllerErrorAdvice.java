package com.kyuwon.booklog.controller.exception;

import com.kyuwon.booklog.dto.error.ErrorRespose;
import com.kyuwon.booklog.errors.UserEmailNotMatchesException;
import com.kyuwon.booklog.errors.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 사용자 HTTP 요청이 실패하는 경우 처리한다.
 */
@ResponseBody
@ControllerAdvice
public class UserControllerErrorAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorRespose handleUserNotFound() {
        return new ErrorRespose("User not Found");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailNotMatchesException.class)
    public ErrorRespose handleUserEmailNotMatches() {
        return new ErrorRespose("User Email Not Matches");
    }
}
