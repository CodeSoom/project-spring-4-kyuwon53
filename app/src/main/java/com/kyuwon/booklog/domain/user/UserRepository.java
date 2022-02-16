package com.kyuwon.booklog.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자를 저장소에 찾기, 저장, 삭제 등의 기능을 제공한다.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * email에 해당하는 사용자를 찾는다.
     *
     * @param email
     * @return email에 해당하는 사용자
     */
    Optional<User> findByEmail(String email);

    /**
     * 등록된 회원이라면 true, 등록된 회원이 아니면 false를 리턴한다.
     *
     * @param email 회원 이메일
     * @return 등록된 회원 true, 미등록은 false
     */
    boolean existsByEmail(String email);

    /**
     * email에 해당하는 사용자를 찾고 탈퇴여부를 확인한다.
     *
     * @param email 회원 이메일
     * @return 탈퇴되지 않은 등록된 회원
     */
    Optional<User> findByEmailAndDeletedIsFalse(String email);
}
