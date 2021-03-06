package com.kyuwon.booklog.service.comments;

import com.kyuwon.booklog.domain.comments.Comments;
import com.kyuwon.booklog.domain.comments.CommentsRepository;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.comments.CommentsData;
import com.kyuwon.booklog.dto.comments.CommentsSaveData;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.errors.CommentNotFoundException;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import com.kyuwon.booklog.errors.UserEmailNotMatchesException;
import com.kyuwon.booklog.service.posts.PostsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("댓글 관리")
class CommentServiceTest {
    private static final String COMMENT_CONTENT = "댓글 내용";
    private static final String EMAIL = "test@email.com";
    private static final String UPDATE_CONTENT = "new댓글 내용";
    private static final String TITLE = "테스트 제목";
    private static final String POST_CONTENT = "테스트 내용";
    private static final String AUTHOR = "테스트 작성자";

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostsService postsService;

    @Autowired
    private CommentsRepository commentsRepository;

    private Posts post;

    @BeforeEach
    public void setPost() {
        post = postsService.save(getPost());
    }

    @AfterEach
    public void cleanUp() {
        commentsRepository.deleteAll();
    }

    @Nested
    @DisplayName("댓글 작성은")
    class Discribe_save {
        Comments comment;

        @Nested
        @DisplayName("댓글을 입력받으면")
        class Context_when_comment {
            @Test
            @DisplayName("저장하고 댓글을 리턴한다.")
            void it_return_comment() {
                comment = commentService.save(getComment(post.getId()));

                assertThat(comment.getPostId()).isEqualTo(post.getId());
                assertThat(comment.getContent()).isEqualTo(COMMENT_CONTENT);
                assertThat(comment.getEmail()).isEqualTo(EMAIL);
            }
        }

        @Nested
        @DisplayName("없는 게시물에 댓글을 작성하면")
        class Context_when_none_post {
            @BeforeEach
            void deletePost() {
                postsService.delete(post.getId());
            }

            @Test
            @DisplayName("찾을 수 없는 게시물이라는 예외를 던진다.")
            void it_throw_PostsNotFoundException() {
                assertThatThrownBy(() -> commentService.save(getComment(post.getId())))
                        .isInstanceOf(PostsNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("댓글 목록 조회")
    class Describe_comment_list {
        @Nested
        @DisplayName("게시물에 댓글이 존재하면")
        class Context_when_exist_comments {
            private static final int COUNT = 10;

            @BeforeEach
            void setComment() {
                for (int i = 0; i < COUNT; i++) {
                    commentService.save(getComment(post.getId()));
                }
            }

            @Test
            @DisplayName("댓글 목록을 리턴한다.")
            void return_comment_list() {
                assertThat(commentService.commentsList(post.getId()))
                        .hasSize(COUNT);
            }
        }

        @Nested
        @DisplayName("게시물에 댓글이 없다면")
        class Context_when_not_exist_comments {

            @BeforeEach
            void deleteComment() {
                commentsRepository.deleteAll();
            }

            @Test
            @DisplayName("댓글 목록을 리턴한다.")
            void return_comment_list() {
                assertThat(commentService.commentsList(post.getId()))
                        .hasSize(0);
                assertThat(commentService.commentsList(post.getId()))
                        .isEqualTo(new ArrayList<>());
            }
        }
    }

    @Nested
    @DisplayName("댓글 수정은")
    class Describe_update_comment {
        private CommentsData commentsData;
        Comments comment;

        @BeforeEach
        void setUp() {
            comment = commentService.save(getComment(post.getId()));
        }

        @BeforeEach
        void setUpdatedData() {
            commentsData = CommentsData.builder()
                    .comment(UPDATE_CONTENT)
                    .email(comment.getEmail())
                    .build();
        }

        @Nested
        @DisplayName("해당 게시물이 존재하고 작성자가 일치할 경우")
        class Context_when_exist_post_matches_email {

            @Test
            @DisplayName("id에 해당하는 댓글을 수정하고 리턴한다.")
            void it_return_update_comment() {
                Long commentId = comment.getId();
                Comments updatedComment = commentService.update(commentId, commentsData);
                assertThat(updatedComment.getContent()).isEqualTo(commentsData.getComment());
            }
        }

        @Nested
        @DisplayName("해당 게시물이 존재하지 않을 경우")
        class Context_when_not_exist_post {
            private CommentsData commentsData;

            @BeforeEach
            void cleanPost() {
                postsService.delete(post.getId());
            }

            @BeforeEach
            void setUpdatedData() {
                commentsData = CommentsData.builder()
                        .comment(UPDATE_CONTENT)
                        .email(comment.getEmail())
                        .build();
            }

            @Test
            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_PostsNotFoundException() {
                Long commentId = comment.getId();
                assertThatThrownBy(
                        () -> commentService.update(commentId, commentsData))
                        .isInstanceOf(PostsNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("댓글 작성자가 아닌 사용자가 수정을 요청하면")
        class Context_when_not_matches_email {
            private CommentsData commentsWrongData;

            @BeforeEach
            void setUpdatedData() {
                commentsWrongData = CommentsData.builder()
                        .comment(UPDATE_CONTENT)
                        .email("xxx" + comment.getEmail())
                        .build();
            }

            @Test
            @DisplayName("이메일이 일치하지 않는 다는 예외를 던진다.")
            void it_throw_UserEmailNotMatchesException() {
                Long commentId = comment.getId();
                assertThatThrownBy(
                        () -> commentService.update(commentId, commentsWrongData))
                        .isInstanceOf(UserEmailNotMatchesException.class);
            }
        }

        @Nested
        @DisplayName("id에 해당하는 댓글이 없다면")
        class Context_when_not_exist_comment {
            @BeforeEach
            void deleteComment() {
                commentsRepository.deleteById(comment.getId());
            }

            @Test
            @DisplayName("id에 해당하는 댓글이 없다는 예외를 던진다.")
            void it_throw_CommentNotFoundException() {
                assertThatThrownBy(
                        () -> commentService.update(comment.getId(), commentsData))
                        .isInstanceOf(CommentNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("댓글 삭제는")
    class Describe_delete_comment {
        private Comments comment;
        private String requestEmail;
        private Long id;

        @BeforeEach
        void setUp() {
            comment = commentService.save(getComment(post.getId()));
            requestEmail = comment.getEmail();
            id = comment.getId();
        }

        @Nested
        @DisplayName("해당 게시물이 존재하고 작성자가 일치할 경우")
        class Context_when_exist_post_matches_email {

            @Test
            @DisplayName("id에 해당하는 댓글을 삭제하고 리턴한다.")
            void it_return_delete_comment() {
                Comments deletedComment = commentService.delete(id, requestEmail);
                assertThat(deletedComment.getId()).isEqualTo(id);
                assertThat(deletedComment.getEmail()).isEqualTo(requestEmail);
            }
        }

        @Nested
        @DisplayName("해당 게시물이 존재하지 않을 경우")
        class Context_when_not_exist_post {

            @BeforeEach
            void cleanPost() {
                postsService.delete(post.getId());
            }

            @Test
            @DisplayName("게시물을 찾을 수 없다는 예외를 던진다.")
            void it_throw_PostsNotFoundException() {
                assertThatThrownBy(
                        () -> commentService.delete(id, requestEmail))
                        .isInstanceOf(PostsNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("댓글 작성자가 아닌 사용자가 삭제를 요청하면")
        class Context_when_not_matches_email {

            @Test
            @DisplayName("이메일이 일치하지 않는 다는 예외를 던진다.")
            void it_throw_UserEmailNotMatchesException() {
                assertThatThrownBy(
                        () -> commentService.delete(id, "xx" + requestEmail))
                        .isInstanceOf(UserEmailNotMatchesException.class);
            }
        }

        @Nested
        @DisplayName("id에 해당하는 댓글이 없다면")
        class Context_when_not_exist_comment {
            @BeforeEach
            void deleteComment() {
                commentsRepository.deleteById(id);
            }

            @Test
            @DisplayName("id에 해당하는 댓글이 없다는 예외를 던진다.")
            void it_throw_CommentNotFoundException() {
                assertThatThrownBy(
                        () -> commentService.delete(id, requestEmail))
                        .isInstanceOf(CommentNotFoundException.class);
            }
        }
    }

    private CommentsSaveData getComment(Long id) {
        return CommentsSaveData.builder()
                .postId(id)
                .content(COMMENT_CONTENT)
                .email(EMAIL)
                .build();
    }

    private PostsSaveRequestData getPost() {
        return PostsSaveRequestData.builder()
                .title(TITLE)
                .content(POST_CONTENT)
                .author(AUTHOR)
                .email("test@gmail.com")
                .build();
    }
}
