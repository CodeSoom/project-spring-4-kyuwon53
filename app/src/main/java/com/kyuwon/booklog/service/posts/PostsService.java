package com.kyuwon.booklog.service.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.domain.posts.PostsRepository;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 게시물을 관리한다.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PostsService {
    private final PostsRepository postsRepository;

    /**
     * 게시물 정보를 받아 저장하고 등록된 게시물을 리턴한다.
     * @param postsSaveRequestData 게시물 정보
     * @return 등록된 게시물
     */
    public Posts save(PostsSaveRequestData postsSaveRequestData) {
        return postsRepository.save(postsSaveRequestData.toEntity());
    }
}
