package com.kyuwon.booklog.dto.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class PostsSaveRequestData {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String author;

    @NotBlank
    private String email;

    @Builder
    public PostsSaveRequestData(String title, String content, String author, String email) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.email = email;
    }

    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .email(email)
                .build();
    }
}
