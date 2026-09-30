package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;
import com.example.forum.repository.UserRepository;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserRepository repository = new InMemoryUserRepository();
        userService = new UserService(repository);
    }

    @Test
    void createUser_shouldCreateUser() {
        User user = new User(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456",
                UserRole.USER
        );

        User created = userService.createUser(user);

        assertEquals(user, created);
        assertEquals("nikita", created.getUsername());
    }

    @Test
    void createUser_shouldRejectDuplicateId() {
        User user = new User(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456",
                UserRole.USER
        );

        userService.createUser(user);

        User duplicate = new User(
                1L,
                "other",
                "other@mail.ru",
                "123456",
                UserRole.USER
        );

        assertThrows(
                IllegalStateException.class,
                () -> userService.createUser(duplicate)
        );
    }

    @Test
    void createUser_shouldRejectInvalidUser() {
        User user = new User(
                1L,
                "",
                "mail@mail.ru",
                "123456",
                UserRole.USER
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(user)
        );
    }
}