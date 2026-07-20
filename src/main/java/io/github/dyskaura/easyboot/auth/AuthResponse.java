package io.github.dyskaura.easyboot.auth;

import io.github.dyskaura.easyboot.user.Role;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String username,
        String nickname,
        Role role
) {
}
