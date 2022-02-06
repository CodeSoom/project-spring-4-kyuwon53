package com.kyuwon.booklog.errors;

public class PostsNotFoundException extends RuntimeException {
    public PostsNotFoundException(Long id) {
        super("해당 게시글이 없습니다. id=" + id);
    }
}
