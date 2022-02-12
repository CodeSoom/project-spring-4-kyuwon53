package com.kyuwon.booklog.service.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.errors.UserEmailDuplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자를 관리한다.
 */
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    /**
     * 회원 정보를 받아 회원을 생성하여 리턴한다.
     *
     * @param saveRequestData 회원 생성 정보
     * @return 생성된 회원
     * @throws UserEmailDuplicationException 회원 이메일이 중복된 경우
     */
    public User signUp(UserSaveRequestData saveRequestData) {
        String email = saveRequestData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }
        //TODO: 비밀번호 인코딩 처리
        return userRepository.save(saveRequestData.toEntity());
    }
}
