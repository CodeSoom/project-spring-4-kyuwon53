package com.kyuwon.booklog.domain.comments;

import com.kyuwon.booklog.domain.posts.BaseTimeEntity;
import com.kyuwon.booklog.dto.comments.CommentsData;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

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
    @Column(name = "COMMENT_ID")
    private Long id;
    /**
     * 게시물 아이디
     */
    @JoinColumn(name = "POST_ID")
    private Long postId;
    /**
     * 댓글 내용
     */
    @Column(name = "COMMENT_COMTENT", nullable = false)
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

    /**
     * 수정 내용을 받아 댓글 내용을 수정한다.
     *
     * @param commentsData 수정 내용
     */
    public void update(CommentsData commentsData) {
        this.content = commentsData.getComment();
    }
}
