package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;
import com.example.forum.repository.UserRepository;

class AuthServiceTest {

    private AuthService authService;
    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        userService = new UserService(userRepository);
        authService = new AuthService(userService, userRepository);
    }

    @Test
    void register_shouldCreateUser() {
        User user = authService.register(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456"
        );

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("nikita", user.getUsername());
        assertEquals("nikita@mail.ru", user.getEmail());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void login_shouldReturnUserWithCorrectPassword() {
        authService.register(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456"
        );

        User loggedIn = authService.login(1L, "123456");

        assertNotNull(loggedIn);
        assertEquals(1L, loggedIn.getId());
        assertEquals("nikita", loggedIn.getUsername());
    }

    @Test
    void login_shouldRejectIncorrectPassword() {
        authService.register(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(1L, "wrong-password")
        );
    }
}