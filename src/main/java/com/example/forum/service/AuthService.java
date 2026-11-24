package com.example.forum.service;

import com.example.forum.models.User;
import com.example.forum.exception.InvalidRequestException;
import com.example.forum.exception.InvalidCredentialsException;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
//@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public User register(String username, String email, String password) {
        User user = new User(null, username, email, password, UserRole.USER);
        User created = userService.create(user);
        log.info("Зарегистрирован пользователь '{}' (id={})", created.getUsername(), created.getId());
        return created;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new InvalidRequestException("Имя пользователя и пароль обязательны.");
        }

        User user = userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> {
                    log.warn("Неудачный вход: пользователь '{}' не найден", username);
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Неудачный вход: неверный пароль пользователя '{}'", username);
            throw new InvalidCredentialsException();
        }
        log.info("Успешный вход пользователя '{}'", user.getUsername());
        return user;
    }
}
