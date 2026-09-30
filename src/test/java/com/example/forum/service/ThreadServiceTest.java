package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.ForumThread;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.MemoryRepository.InMemoryThreadRepository;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;

class ThreadServiceTest {

    private ThreadService threadService;
    private User user;

    @BeforeEach
    void setUp() {
        var userRepository = new InMemoryUserRepository();
        var threadRepository = new InMemoryThreadRepository();

        user = new User(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456",
                UserRole.USER
        );

        userRepository.create(user);

        threadService = new ThreadService(
                threadRepository,
                userRepository
        );
    }

    @Test
    void createThread_shouldCreateAndAttachToAuthor() {
        ForumThread thread = new ForumThread(
                1L,
                user,
                "Go programming"
        );

        ForumThread created = threadService.createThread(thread);

        assertEquals(thread, created);
        assertTrue(user.getThreads().contains(thread));
    }

    @Test
    void searchThreads_shouldFindByTitle() {
        threadService.createThread(
                new ForumThread(1L, user, "Go programming")
        );

        threadService.createThread(
                new ForumThread(2L, user, "Java programming")
        );

        var result = threadService.searchThreads("go");

        assertEquals(1, result.size());
        assertEquals("Go programming", result.get(0).getTitle());
    }
}