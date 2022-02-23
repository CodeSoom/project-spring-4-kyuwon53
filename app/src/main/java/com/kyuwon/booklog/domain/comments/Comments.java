package com.kyuwon.booklog.domain.comments;

import com.kyuwon.booklog.domain.posts.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 댓글 정보를 저장하는 객체
 */
@Getter
@NoArgsConstructor
@Entity
public class Comments extends BaseTimeEntity {
    /**
     * 댓글 아이디
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    /**
     * 게시물 아이디
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Long postId;
    /**
     * 댓글 내용
     */
    @Column(name = "comment_content", nullable = false)
    private String content;
    /**
     * 작성자 이메일
     */
    @Column(nullable = false)
    private String email;

    @Builder
    public Comments(Long id, Long postId, String content, String email) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.email = email;
    }
}
