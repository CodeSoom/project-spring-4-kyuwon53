package com.kyuwon.booklog.service.comments;

import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.comments.CommentsRepository;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.comments.CommentsData;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.errors.CommentNotFoundException;
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

        getPost(postId);

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
     * @throws CommentNotFoundException     id에 해당하는 댓글을 찾지 못한 경우
     * @throws UserEmailNotMatchesException 요청 이메일과 댓글 작성자 이메일이 다를 경우
     * @throws PostsNotFoundException       해당 게시물이 존재하지 않을 경우
     */
    public Comments update(
            Long id,
            CommentsData commentsData) {

        Comments comment = getComment(id);

        getPost(comment.getPostId());

        String commentAuthorEmail = comment.getEmail();
        String requestEmail = commentsData.getEmail();

        if (!commentAuthorEmail.equals(requestEmail)) {
            throw new UserEmailNotMatchesException(requestEmail);
        }

        comment.update(commentsData);

        return comment;
    }

    /**
     * id에 해당하는 댓글을 삭제하고 리턴한다.
     *
     * @param id    삭제할 댓글 id
     * @param email 댓글 삭제를 요청한 이메일
     * @return 삭제된 댓글
     * @throws CommentNotFoundException     id에 해당하는 댓글을 찾지 못한 경우
     * @throws UserEmailNotMatchesException 이메일이 일치하지 않을 경우
     * @throws PostsNotFoundException       게시물을 찾지 못했을 경우
     */
    public Comments delete(Long id, String email) {
        Comments comment = getComment(id);

        getPost(comment.getId());

        String commentAuthorEmail = comment.getEmail();

        if (!commentAuthorEmail.equals(email)) {
            throw new UserEmailNotMatchesException(email);
        }

        commentsRepository.delete(comment);
        return comment;
    }

    /**
     * id에 해당하는 댓글을 리턴한다.
     *
     * @param id 댓글 id
     * @return id에 해당하는 댓글
     * @throws CommentNotFoundException id에 해당하는 댓글을 찾지 못한 경우
     */
    private Comments getComment(Long id) {
        return commentsRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    /**
     * id에 해당하는 게시물을 찾는다.
     *
     * @param postId 게시물 식별자
     * @throws PostsNotFoundException 게시물을 찾지 못했을 경우
     */
    private void getPost(Long postId) {
        postsRepository.findById(postId)
                .orElseThrow(() -> new PostsNotFoundException(postId));
    }
}
