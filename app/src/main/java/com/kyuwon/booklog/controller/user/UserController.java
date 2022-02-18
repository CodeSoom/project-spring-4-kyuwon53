package com.kyuwon.booklog.controller.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;

    /**
     * 회원을 생성하고 리턴한다.
     *
     * @param saveRequestData 회원 생성 정보
     * @return 생성된 회원
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    User signUp(@RequestBody @Valid UserSaveRequestData saveRequestData) {
        return userService.signUp(saveRequestData);
    }

    /**
     * 회원 정보를 수정하고 리턴한다.
     *
     * @param id             수정할 회원 id
     * @param userModifyData 회원 수정 정보
     * @return 수정된 회원
     */
    @PatchMapping("/{id}")
    UserData update(
            @PathVariable Long id,
            @RequestBody UserData userModifyData
    ) {
        String email = userService.getUserEmailById(id);
        User user = userService.updateUser(email, userModifyData);
        return user.toUserData();
    }

    /**
     * 회원을 탈퇴처리하고 리턴한다.
     *
     * @param id 탈퇴할 회원 id
     * @return 탈퇴한 회원
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserData delete(@PathVariable Long id) {
        String email = userService.getUserEmailById(id);
        User user = userService.deleteUser(email);
        return user.toUserData();
    }

    /**
     * 요청받은 id에 해당하는 회원을 조회한다.
     *
     * @param id 조회할 회원 id
     * @return 조회된 회원
     */
    @GetMapping("/{id}")
    public UserData detail(@PathVariable Long id) {
        String email = userService.getUserEmailById(id);
        User user = userService.detailUser(email);
        return user.toUserData();
    }

    /**
     * 등록된 회원 목록을 리턴한다.
     *
     * @return 회원 전체 목록
     */
    @GetMapping
    public List<User> list() {
        return userService.userList();
    }
}
