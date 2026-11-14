package com.example.forum.service;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.UserRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.CommentRepository;
import com.example.forum.exception.UserHasDependenciesException;
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
class UserServiceTest {
    @Mock UserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock ThreadRepository threads;
    @Mock PostRepository posts;
    @Mock CommentRepository comments;
    @InjectMocks UserService service;

    @Test
    void createEncodesPasswordAndSavesUser() {
        User input = new User(99L, " nikita ", "NIKITA@mail.ru", "123456", UserRole.USER);
        when(repository.existsByUsername("nikita")).thenReturn(false);
        when(repository.existsByEmail("nikita@mail.ru")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = service.create(input);

        assertEquals(1L, result.getId());
        assertEquals("nikita", result.getUsername());
        assertEquals("nikita@mail.ru", result.getEmail());
        assertEquals("encoded", result.getPassword());
        verify(repository).save(input);
    }

    @Test
    void createRejectsDuplicateUsername() {
        when(repository.existsByUsername("nikita")).thenReturn(true);
        User input = new User(null, "nikita", "n@mail.ru", "123456", UserRole.USER);

        assertThrows(IllegalArgumentException.class, () -> service.create(input));
        verify(repository, never()).save(any());
    }

    @Test
    void findRejectsMissingUser() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.find(42L));
    }

    @Test
    void deleteRejectsUserWhenUserHasContent() {
        User user = new User(7L, "nikita", "n@mail.ru", "hash", UserRole.USER);
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(threads.countByAuthorId(7L)).thenReturn(1L);
        when(posts.countByAuthorId(7L)).thenReturn(2L);
        when(comments.countByAuthorId(7L)).thenReturn(3L);

        assertThrows(UserHasDependenciesException.class, () -> service.delete(7L));
        verify(repository, never()).delete(any(User.class));
    }

    @Test
    void deleteRemovesUserWhenUserHasNoContent() {
        User user = new User(7L, "nikita", "n@mail.ru", "hash", UserRole.USER);
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(threads.countByAuthorId(7L)).thenReturn(0L);
        when(posts.countByAuthorId(7L)).thenReturn(0L);
        when(comments.countByAuthorId(7L)).thenReturn(0L);

        service.delete(7L);

        verify(repository).delete(user);
    }
}
