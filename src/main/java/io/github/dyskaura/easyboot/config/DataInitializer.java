package io.github.dyskaura.easyboot.config;

import io.github.dyskaura.easyboot.user.Role;
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
    }
}
