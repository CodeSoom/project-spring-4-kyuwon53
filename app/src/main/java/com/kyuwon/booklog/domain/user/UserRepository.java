package com.kyuwon.booklog.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자를 저장소에 찾기, 저장, 삭제 등의 기능을 제공한다.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * email에 해당하는 사용자를 찾는다.
     * @param email
     * @return email에 해당하는 사용자
     */
    Optional<User> findByEmail(String email);
}
