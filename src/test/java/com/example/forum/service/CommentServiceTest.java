package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.Comment;
import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.MemoryRepository.InMemoryCommentRepository;
import com.example.forum.repository.MemoryRepository.InMemoryPostRepository;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;

class CommentServiceTest {

    private CommentService commentService;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        var userRepository = new InMemoryUserRepository();
        var postRepository = new InMemoryPostRepository();
        var commentRepository = new InMemoryCommentRepository();

        user = new User(
                1L,
                "nikita",
                "nikita@mail.ru",
                "123456",
                UserRole.USER
        );

        userRepository.create(user);

        ForumThread thread = new ForumThread(
                1L,
                user,
                "Go"
        );

        post = new Post(
                1L,
                "Hello Go",
                user,
                thread
        );

        postRepository.create(post);

        commentService = new CommentService(
                commentRepository,
                userRepository,
                postRepository
        );
    }

    @Test
    void createComment_shouldAttachToAuthorAndPost() {
        Comment comment = new Comment(
                1L,
                "Nice post",
                user,
                post
        );

        Comment created = commentService.createComment(comment);

        assertEquals(comment, created);
        assertTrue(user.getComments().contains(comment));
        assertTrue(post.getComments().contains(comment));
    }

    @Test
    void deleteComment_shouldRemoveFromAuthorAndPost() {
        Comment comment = new Comment(
                1L,
                "Nice post",
                user,
                post
        );

        commentService.createComment(comment);

        commentService.deleteComment(1L);

        assertFalse(user.getComments().contains(comment));
        assertFalse(post.getComments().contains(comment));
        assertTrue(commentService.getComment(1L).isEmpty());
    }
}