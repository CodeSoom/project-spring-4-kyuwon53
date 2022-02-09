package com.kyuwon.booklog.dto.error;

import lombok.Getter;

/**
 * 에러 응답 메세지 모델
 */
@Getter
public class ErrorRespose {
    private String message;

    public ErrorRespose(String message) {
        this.message = message;
    }
}
