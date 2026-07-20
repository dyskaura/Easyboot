package io.github.dyskaura.easyboot.menu;

import io.github.dyskaura.easyboot.common.ApiResponse;
import io.github.dyskaura.easyboot.log.Operation;
import io.github.dyskaura.easyboot.user.Role;
import io.github.dyskaura.easyboot.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;
    private final UserRepository userRepository;

    @GetMapping("/current")
    public ApiResponse<List<Menu>> current(Authentication authentication) {
        Role role = userRepository.findByUsername(authentication.getName()).orElseThrow().getRole();
        return ApiResponse.success(service.current(role));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Menu>> all() {
        return ApiResponse.success(service.all());
    }

    @PostMapping
    @Operation("创建菜单")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Menu> create(@Valid @RequestBody MenuRequest request) {
        return ApiResponse.success("菜单创建成功", service.save(null, request));
    }

    @PutMapping("/{id}")
    @Operation("更新菜单")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Menu> update(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return ApiResponse.success("菜单更新成功", service.save(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation("删除菜单")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("菜单删除成功", null);
    }
}
