package com.kyuwon.booklog.controller.exception;

import com.kyuwon.booklog.dto.error.ErrorRespose;
import com.kyuwon.booklog.errors.CommentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 댓글 HTTP 요청이 실패하는 경우 처리한다.
 */
@ResponseBody
@ControllerAdvice
public class CommentControllerErrorAdvice {
    /**
     * 댓글을 찾지 못한 경우에 대한 에러 응답을 리턴한다.
     *
     * @return 댓글을 찾을 수 없다는 응답.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public ErrorRespose handleCommentNotFound() {
        return new ErrorRespose("Comment not found");
    }
}
