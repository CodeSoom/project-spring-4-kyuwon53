package com.kyuwon.booklog.service.comments;

import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.comments.CommentsRepository;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * 댓글을 관리한다.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    /**
     * 댓글을 저장하고 등록된 댓글을 리턴한다.
     *
     * @param commentsSaveData 댓글 정보
     * @return 등록된 댓글
     */
    public Comments save(CommentsSaveData commentsSaveData) {
        Long postId = commentsSaveData.getPostId();

        postsRepository.findById(postId)
                .orElseThrow(() -> new PostsNotFoundException(postId));

        return commentsRepository.save(commentsSaveData.toEntity());
    }

    /**
     * 해당 게시물의 댓글 목록을 리턴한다.
     *
     * @param postId 게시물 아이디
     * @return 댓글 목록
     */
    public List<Comments> commentsList(Long postId) {

        return commentsRepository.findAllByPostId(postId);
    }
}
