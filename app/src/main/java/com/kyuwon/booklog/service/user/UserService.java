package com.kyuwon.booklog.service.user;

import com.kyuwon.booklog.domain.user.User;
import com.kyuwon.booklog.domain.user.UserRepository;
import com.kyuwon.booklog.dto.user.UserData;
import com.kyuwon.booklog.dto.user.UserSaveRequestData;
import com.kyuwon.booklog.errors.UserEmailDuplicationException;
import com.kyuwon.booklog.errors.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자를 관리한다.
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        User user = saveRequestData.toEntity();

        user.encodePassword(saveRequestData.getPassword(), passwordEncoder);

        return userRepository.save(user);
    }

    /**
     * 이메일에 해당하는 사용자를 수정하고 리턴한다.
     *
     * @param email          회원 이메일
     * @param userUpdateData 수정할 사용자 정보
     * @return 수정된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User updateUser(String email, UserData userUpdateData) {
        User user = userRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.changeUser(userUpdateData);
        user.encodePassword(userUpdateData.getPassword(), passwordEncoder);
        return user;
    }

    /**
     * 이메일에 해당하는 사용자를 탈퇴 처리하고 리턴한다.
     *
     * @param email 사용자 이메일
     * @return 삭제된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User deleteUser(String email) {
        User user = userRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.deleted();
        return user;
    }
}
