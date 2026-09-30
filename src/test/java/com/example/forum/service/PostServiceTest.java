package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.MemoryRepository.InMemoryPostRepository;
import com.example.forum.repository.MemoryRepository.InMemoryThreadRepository;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;

class PostServiceTest {

    private PostService postService;
    private User user;
    private ForumThread thread;

    @BeforeEach
    void setUp() {
        var userRepository = new InMemoryUserRepository();
        var threadRepository = new InMemoryThreadRepository();
        var postRepository = new InMemoryPostRepository();

        user = new User(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456",
                UserRole.USER
        );

        userRepository.create(user);

        thread = new ForumThread(
                1L,
                user,
                "Go"
        );

        threadRepository.create(thread);

        postService = new PostService(
                postRepository,
                userRepository,
                threadRepository
        );
    }

    @Test
    void createPost_shouldAttachToAuthorAndThread() {
        Post post = new Post(
                1L,
                "Hello Go",
                user,
                thread
        );

        Post created = postService.createPost(post);

        assertEquals(post, created);
        assertTrue(user.getPosts().contains(post));
        assertTrue(thread.getPosts().contains(post));
    }

    @Test
    void searchPosts_shouldFindByContent() {
        postService.createPost(
                new Post(1L, "Hello Go", user, thread)
        );

        postService.createPost(
                new Post(2L, "Hello Java", user, thread)
        );

        var result = postService.searchPosts("go");

        assertEquals(1, result.size());
        assertEquals("Hello Go", result.get(0).getContent());
    }
}