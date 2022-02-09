package com.kyuwon.booklog.controller.Exception;

import com.kyuwon.booklog.dto.errors.ErrorRespose;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 게시물 HTTP 요청이 실패하는 경우 처리한다.
 */
@ResponseBody
@ControllerAdvice
public class PostControllerErrorAdvice {
    /**
     * 게시물을 찾지 못한 경우에 대한 에러 응답을 리턴한다.
     * @return 게시물을 찾을 수 없다는 응답.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PostsNotFoundException.class)
    public ErrorRespose handlePostNotFound() {
        return new ErrorRespose("Post not found");
    }
}
