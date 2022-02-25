package com.kyuwon.booklog.dto.comments;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 데이터 모델
 */
@Getter
@NoArgsConstructor
public class CommentsData {
    private Long id;
    private String comment;
    private String email;

    @Builder
    public CommentsData(Long id, String comment, String email) {
        this.id = id;
        this.comment = comment;
        this.email = email;
    }
}
