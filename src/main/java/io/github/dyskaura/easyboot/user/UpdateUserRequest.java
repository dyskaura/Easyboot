package io.github.dyskaura.easyboot.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(max = 50) String nickname,
        @Email @Size(max = 100) String email,
        @NotNull Role role,
        @NotNull Boolean enabled
) {
}
