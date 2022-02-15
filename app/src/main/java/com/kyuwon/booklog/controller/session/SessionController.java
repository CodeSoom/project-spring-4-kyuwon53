package com.kyuwon.booklog.controller.session;

import com.kyuwon.booklog.dto.session.SessionResponseData;
import com.kyuwon.booklog.dto.user.UserLoginData;
import com.kyuwon.booklog.service.session.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 사용자 인증에 대한 HTTP 요청을 처리한다.
 */
@RequestMapping("/session")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SessionController {
    private final AuthenticationService authenticationService;

    /**
     * 사용자를 인증하고 결과를 리턴한다.
     *
     * @return 인증 결과
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseData login(@RequestBody @Valid UserLoginData userLoginData) {
        String accessToken = authenticationService.login(userLoginData);

        return SessionResponseData.builder()
                .accessToken(accessToken)
                .build();
    }
    //TODO read => 세션 정보
    //TODO update => Token 재발급
    //TODO delete => 로그아웃 Token 무효
}
