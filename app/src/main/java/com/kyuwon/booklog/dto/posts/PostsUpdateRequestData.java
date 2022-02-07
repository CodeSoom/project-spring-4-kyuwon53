package com.kyuwon.booklog.dto.posts;

import com.kyuwon.booklog.domain.posts.PostUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시물 수정 모델
 * 게시물 수정은 제목과 내용만 가능하다.
 */
@Getter
@NoArgsConstructor
public class PostsUpdateRequestData implements PostUpdateRequest {
    private String title;
    private String content;

    @Builder
    public PostsUpdateRequestData(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
