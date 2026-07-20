package io.github.dyskaura.easyboot.auth;

import io.github.dyskaura.easyboot.common.ApiResponse;
import io.github.dyskaura.easyboot.user.UserRepository;
import io.github.dyskaura.easyboot.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .map(UserResponse::from)
                .map(ApiResponse::success)
                .orElseThrow();
    }
}
