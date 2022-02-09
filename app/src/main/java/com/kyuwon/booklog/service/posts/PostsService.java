package com.kyuwon.booklog.service.posts;

import com.kyuwon.booklog.domain.posts.PostUpdateRequest;
import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.errors.PostsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 게시물을 관리한다.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PostsService {
    private final PostsRepository postsRepository;

    /**
     * 게시물 전체 목록을 리턴한다.
     *
     * @return 게시물 전체 목록
     */
    public List<Posts> getPosts() {
        return postsRepository.findAll();
    }

    /**
     * 게시물 정보를 받아 저장하고 등록된 게시물을 리턴한다.
     *
     * @param postsSaveRequestData 게시물 정보
     * @return 등록된 게시물
     */
    public Posts save(PostsSaveRequestData postsSaveRequestData) {
        return postsRepository.save(postsSaveRequestData.toEntity());
    }

    /**
     * 게시물 수정 정보를 받아 수정하고 수정된 게시물을 리턴한다.
     *
     * @param id          해당 게시물 id
     * @param requestData 수정할 정보
     * @return 수정된 게시물
     * @throws PostsNotFoundException id에 해당하는 게시물을 찾지 못한 경우
     */
    public Posts update(Long id,
                        PostUpdateRequest requestData) {
        Posts posts = getPost(id);

        posts.update(requestData);

        return posts;
    }

    /**
     * id에 해당하는 게시물을 리턴한다.
     *
     * @param id 해당 게시물 id
     * @return 조회한 게시물
     * @throws PostsNotFoundException id에 해당하는 게시물을 찾지 못한 경우
     */
    public Posts getPost(Long id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new PostsNotFoundException(id));
    }
}
