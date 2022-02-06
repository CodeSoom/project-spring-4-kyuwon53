package com.kyuwon.booklog.controller.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.service.posts.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시물 HTTP요청을 처리한다.
 */
@RequestMapping("/posts")
@RequiredArgsConstructor
@RestController
@CrossOrigin
public class PostsController {

    private final PostsService postsService;

    /**
     * 게시물을 등록하고, 상태코드 201을 응답한다.
     *
     * @param requestData 게시물 정보
     * @return 등록된 게시물
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Posts create(@RequestBody PostsSaveRequestData requestData) {
        return postsService.save(requestData);
    }
}
