package com.kyuwon.booklog.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자의 권한.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    GUSET("ROLE_GUEST","손님"),
    USER("ROLE_USER","일반 사용자"),
    ADMIN("ROLE_ADMIN","관리자");

    private final String key;
    private final String title;
}
