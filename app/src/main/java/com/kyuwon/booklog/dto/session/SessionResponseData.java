package com.kyuwon.booklog.dto.session;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 세션 응답 데이터
 */
@Getter
@NoArgsConstructor
public class SessionResponseData {
    /**
     * 인증 토큰
     */
    private String accessToken;

    @Builder
    public SessionResponseData(String accessToken) {
        this.accessToken = accessToken;
    }
}
