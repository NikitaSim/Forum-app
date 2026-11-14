package com.example.forum.service;

import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.PostRepository;
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
class CommentServiceTest {
    @Mock CommentRepository repository;
    @Mock UserRepository users;
    @Mock PostRepository posts;
    @InjectMocks CommentService service;

    @Test
    void createLoadsAuthorAndPost() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", null);
        Post post = new Post(2L, "Post body", author, null);
        when(users.findById(1L)).thenReturn(Optional.of(author));
        when(posts.findById(2L)).thenReturn(Optional.of(post));
        when(repository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        Comment result = service.create(new Comment(null, "Comment", new User(1L, null, null, null, null),
                new Post(2L, null, null, null)));

        assertSame(author, result.getAuthor());
        assertSame(post, result.getPost());
    }
}
