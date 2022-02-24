package com.kyuwon.booklog.controller.comments;

import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.service.comments.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 댓글 HTTP 요청을 처리한다.
 */
@RequestMapping("/comments")
@RequiredArgsConstructor
@RestController
@CrossOrigin
public class CommentsController {
    private final CommentService commentService;

    /**
     * 댓글을 등록하고, 상태코드 201을 응답한다.
     *
     * @param commentsSaveData 댓글 정보
     * @return 등록된 댓글
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comments create(@RequestBody CommentsSaveData commentsSaveData) {
        return commentService.save(commentsSaveData);
    }

    /**
     * 게시물 id에 해당하는 댓글 목록을 리턴한다.
     *
     * @param postId 게시물 id
     * @return 게시물에 댓글 목록
     */
    @GetMapping("/{postId}")
    public List<Comments> list(@PathVariable Long postId) {
        return commentService.commentsList(postId);
    }
}
