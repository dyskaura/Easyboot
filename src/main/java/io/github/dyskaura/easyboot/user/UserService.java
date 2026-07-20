package io.github.dyskaura.easyboot.user;

import io.github.dyskaura.easyboot.common.BusinessException;
import io.github.dyskaura.easyboot.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = keyword == null || keyword.isBlank()
                ? userRepository.findAll(pageable)
                : userRepository.findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
                        keyword.trim(), keyword.trim(), pageable
                );
        return PageResponse.from(users.map(UserResponse::from));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        user.setNickname(request.nickname().trim());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setEnabled(request.enabled());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id, String currentUsername) {
        User user = findUser(id);
        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "不能删除当前登录账号");
        }
        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "用户不存在"));
    }
}
