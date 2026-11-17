package com.example.forum.config;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    AdminInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new AdminInitializer(userRepository, userService, "admin", "admin@forum.local", "admin123");
    }

    @Test
    void createsAdminWhenMissing() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);

        initializer.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).create(captor.capture());
        assertEquals(UserRole.ADMIN, captor.getValue().getRole());
        assertEquals("admin", captor.getValue().getUsername());
    }

    @Test
    void skipsWhenAdminExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        initializer.run();

        verify(userService, never()).create(any(User.class));
    }
}
