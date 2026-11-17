package com.example.forum.config;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;
    private final String username;
    private final String email;
    private final String password;

    public AdminInitializer(UserRepository userRepository,
                            UserService userService,
                            @Value("${app.security.admin.username}") String username,
                            @Value("${app.security.admin.email}") String email,
                            @Value("${app.security.admin.password}") String password) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(username)) {
            log.info("Администратор '{}' уже существует, создание пропущено", username);
            return;
        }
        userService.create(new User(null, username, email, password, UserRole.ADMIN));
        log.info("Создан администратор по умолчанию '{}'", username);
    }
}
