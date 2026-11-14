package com.example.forum.service;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock PostRepository repository;
    @Mock UserRepository users;
    @Mock ThreadRepository threads;
    @InjectMocks PostService service;

    @Test
    void createLoadsAuthorAndThread() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", null);
        ForumThread thread = new ForumThread(2L, author, "Go");
        when(users.findById(1L)).thenReturn(Optional.of(author));
        when(threads.findById(2L)).thenReturn(Optional.of(thread));
        when(repository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = service.create(new Post(null, "Hello", new User(1L, null, null, null, null),
                new ForumThread(2L, null, null)));

        assertSame(author, result.getAuthor());
        assertSame(thread, result.getThread());
        assertEquals("Hello", result.getContent());
    }
}
