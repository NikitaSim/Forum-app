package com.example.forum.service;

import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.Vote;
import com.example.forum.models.VoteType;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import com.example.forum.repository.VoteRepository;
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
class VoteServiceTest {
    @Mock VoteRepository repository;
    @Mock UserRepository users;
    @Mock PostRepository posts;
    @InjectMocks VoteService service;

    @Test
    void createRejectsDuplicateVote() {
        when(repository.findByUserIdAndPostId(1L, 2L)).thenReturn(Optional.of(new Vote()));
        Vote vote = new Vote(null, new User(1L, null, null, null, null), new Post(2L, null, null, null), VoteType.UP);

        assertThrows(IllegalArgumentException.class, () -> service.create(vote));
        verify(repository, never()).save(any());
    }

    @Test
    void createSavesVoteWhenUserAndPostExist() {
        User user = new User(1L, "n", "n@mail.ru", "hash", null);
        Post post = new Post(2L, "body", user, null);
        when(repository.findByUserIdAndPostId(1L, 2L)).thenReturn(Optional.empty());
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(posts.findById(2L)).thenReturn(Optional.of(post));
        when(repository.save(any(Vote.class))).thenAnswer(inv -> inv.getArgument(0));

        Vote result = service.create(new Vote(null, new User(1L, null, null, null, null),
                new Post(2L, null, null, null), VoteType.UP));

        assertSame(user, result.getUser());
        assertSame(post, result.getPost());
        assertEquals(VoteType.UP, result.getType());
    }
}
