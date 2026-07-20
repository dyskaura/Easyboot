package io.github.dyskaura.easyboot.menu;

import io.github.dyskaura.easyboot.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuRequest(
        Long parentId,
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 100) String path,
        @Size(max = 50) String icon,
        @NotNull Role requiredRole,
        int sortOrder,
        boolean enabled
) {
}
