package com.kyuwon.booklog.service.comments;

import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.comments.CommentsRepository;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.comments.CommentsData;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import com.kyuwon.booklog.errors.UserEmailNotMatchesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
     * @throws PostsNotFoundException 해당 게시물이 존재하지 않을 경우
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

    /**
     * id에 해당하는 댓글을 수정하고 리턴한다.
     *
     * @param id           댓글 식별자
     * @param commentsData 댓글 수정 내용
     * @return 수정된 댓글
     * @throws UserEmailNotMatchesException 요청 이메일과 댓글 작성자 이메일이 다를 경우
     * @throws PostsNotFoundException       해당 게시물이 존재하지 않을 경우
     */
    public Comments update(
            Long id,
            CommentsData commentsData) {

        Comments comment = commentsRepository.getById(id);

        postsRepository.findById(comment.getPostId())
                .orElseThrow(
                        () -> new PostsNotFoundException(comment.getPostId())
                );

        String commentAuthorEmail = comment.getEmail();
        String requestEmail = commentsData.getEmail();

        if (!commentAuthorEmail.equals(requestEmail)) {
            throw new UserEmailNotMatchesException(requestEmail);
        }

        comment.update(commentsData);

        return comment;
    }
}
