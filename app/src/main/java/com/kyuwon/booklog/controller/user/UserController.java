package com.kyuwon.booklog.controller.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        return getUserResultData(user);
    }

    /**
     * 사용자 요청 결과를 리턴한다.
     *
     * @param user 사용자 정보
     * @return 응답 결과
     */
    private UserData getUserResultData(User user) {
        return UserData.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .picture(user.getPicture())
                .build();
    }
}
