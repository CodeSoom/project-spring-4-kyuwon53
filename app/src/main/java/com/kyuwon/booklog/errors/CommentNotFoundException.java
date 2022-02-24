package com.kyuwon.booklog.errors;

/**
 * id에 해당하는 댓글이 없을 경우
 */
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long id) {
        super("id에 해당하는 댓글이 없습니다. id: " + id);
    }
}
