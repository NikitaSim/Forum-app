package com.example.forum.service;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserService userService;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AuthService authService;

    @Test
    void registerCreatesRegularUser() {
        when(userService.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User result = authService.register("nikita", "nikita@mail.ru", "123456");

        assertEquals("nikita", result.getUsername());
        assertEquals(UserRole.USER, result.getRole());
        verify(userService).create(any(User.class));
    }

    @Test
    void loginReturnsUserWhenPasswordMatches() {
        User user = new User(1L, "nikita", "n@mail.ru", "$2a$hash", UserRole.USER);
        when(userRepository.findByUsernameIgnoreCase("nikita")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "$2a$hash")).thenReturn(true);

        assertSame(user, authService.login("nikita", "secret"));
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = new User(1L, "nikita", "n@mail.ru", "$2a$hash", UserRole.USER);
        when(userRepository.findByUsernameIgnoreCase("nikita")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login("nikita", "wrong"));
    }
}
