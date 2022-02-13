package com.kyuwon.booklog.controller.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.dto.user.UserResultData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    UserResultData signUp(@RequestBody @Valid UserSaveRequestData saveRequestData) {
        User user = userService.signUp(saveRequestData);
        return getUserResultData(user);
    }

    /**
     * 사용자 요청 결과를 리턴한다.
     *
     * @param user 사용자 정보
     * @return 응답 결과
     */
    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .build();
    }
}
