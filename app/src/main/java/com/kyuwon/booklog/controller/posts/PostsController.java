package com.kyuwon.booklog.controller.posts;

import com.kyuwon.booklog.domain.posts.Posts;
import com.kyuwon.booklog.dto.posts.PostsSaveRequestData;
import com.kyuwon.booklog.dto.posts.PostsUpdateRequestData;
import com.kyuwon.booklog.service.posts.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * 등록된 게시물의 전체 목록을 리턴한다.
     *
     * @return 게시물 전체 목록
     */
    @GetMapping
    public List<Posts> list() {
        return postsService.getPosts();
    }

    @GetMapping("/{id}")
    public Posts detail(@PathVariable Long id) {
        return postsService.getPost(id);
    }

    /**
     * 게시물을 등록하고, 상태코드 201을 응답한다.
     *
     * @param requestData 게시물 정보
     * @return 등록된 게시물
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public Posts create(@RequestBody PostsSaveRequestData requestData) {
        return postsService.save(requestData);
    }

    /**
     * 요청받은 id의 게시물을 수정하고 리턴한다.
     *
     * @param id          수정할 게시물 id
     * @param requestData 수정된 게시물 정보
     * @return 수정된 게시물
     */
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Posts update(@PathVariable Long id,
                        @RequestBody PostsUpdateRequestData requestData) {
        return postsService.update(id, requestData);
    }

    /**
     * 요청받은 id의 게시물을 삭제하고 리턴한다.
     *
     * @param id 삭제할 게시물 id
     * @return 삭제된 게시물
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public Posts delete(@PathVariable Long id) {
        return postsService.delete(id);
    }
}
