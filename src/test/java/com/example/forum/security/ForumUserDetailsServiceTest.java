package com.example.forum.security;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumUserDetailsServiceTest {
    @Mock UserRepository userRepository;
    @InjectMocks ForumUserDetailsService service;

    @Test
    void loadsUserWithRoleAuthority() {
        User user = new User(1L, "admin", "a@mail.ru", "$2a$hash", UserRole.ADMIN);
        when(userRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals("$2a$hash", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void throwsWhenUserMissing() {
        when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ghost"));
    }
}
