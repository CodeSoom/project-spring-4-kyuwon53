package com.kyuwon.booklog.domain.posts;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 게시물 작성, 수정시 시간을 관리
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {
    /**
     * 게시물 작성 시간
     */
    @CreatedDate
    private LocalDateTime createdDate;
    /**
     * 게시물 수정 시간
     */
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
