package io.github.dyskaura.easyboot.config;

import io.github.dyskaura.easyboot.user.Role;
import io.github.dyskaura.easyboot.menu.Menu;
import io.github.dyskaura.easyboot.menu.MenuRepository;
import io.github.dyskaura.easyboot.user.User;
import io.github.dyskaura.easyboot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("EasyBoot123"))
                    .nickname("超级管理员")
                    .email("admin@easyboot.dev")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build());
        }
        if (menuRepository.count() == 0) {
            menuRepository.save(Menu.builder().name("首页").path("/dashboard").icon("home")
                    .requiredRole(Role.USER).sortOrder(1).enabled(true).build());
            menuRepository.save(Menu.builder().name("用户管理").path("/system/users").icon("users")
                    .requiredRole(Role.ADMIN).sortOrder(10).enabled(true).build());
            menuRepository.save(Menu.builder().name("字典管理").path("/system/dictionaries").icon("book")
                    .requiredRole(Role.ADMIN).sortOrder(20).enabled(true).build());
            menuRepository.save(Menu.builder().name("操作日志").path("/system/logs").icon("history")
                    .requiredRole(Role.ADMIN).sortOrder(30).enabled(true).build());
        }
    }
}
