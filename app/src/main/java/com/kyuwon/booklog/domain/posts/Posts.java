package com.kyuwon.booklog.domain.posts;

import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 게시물 정보를 저장하는 객체
 */
@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity {
    /**
     * 게시물 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시물 제목
     */
    @Column(length = 500, nullable = false)
    private String title;

    /**
     * 게시물 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 게시물 작성자
     */
    @Column(nullable = false)
    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    /**
     * 제목과 내용을 받아 정보를 수정한다.
     * @param title 수정할 제목
     * @param content 수정할 내용
     */
    public void update(PostsUpdateRequestData updateRequestData) {
        this.title = updateRequestData.getTitle();
        this.content = updateRequestData.getContent();
    }
}

