package com.example.forum.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.models.Vote;
import com.example.forum.models.VoteType;
import com.example.forum.repository.MemoryRepository.InMemoryPostRepository;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;
import com.example.forum.repository.MemoryRepository.InMemoryVoteRepository;

class VoteServiceTest {

    private VoteService voteService;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        var userRepository = new InMemoryUserRepository();
        var postRepository = new InMemoryPostRepository();
        var voteRepository = new InMemoryVoteRepository();

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

        voteService = new VoteService(
                voteRepository,
                userRepository,
                postRepository
        );
    }

    @Test
    void createVote_shouldCreateVoteAndAttachToPost() {
        Vote vote = new Vote(
                1L,
                user,
                post,
                VoteType.UP
        );

        Vote created = voteService.createVote(vote);

        assertEquals(vote, created);
        assertTrue(post.getVotes().contains(vote));
        assertEquals(1, voteService.getPostRating(1L));
    }

    @Test
    void createVote_shouldRejectDuplicateVote() {
        voteService.createVote(
                new Vote(1L, user, post, VoteType.UP)
        );

        Vote duplicate = new Vote(
                2L,
                user,
                post,
                VoteType.DOWN
        );

        assertThrows(
                IllegalStateException.class,
                () -> voteService.createVote(duplicate)
        );
    }

    @Test
    void changeVote_shouldChangeTypeAndRating() {
        voteService.createVote(
                new Vote(1L, user, post, VoteType.UP)
        );

        voteService.changeVote(
                1L,
                1L,
                VoteType.DOWN
        );

        assertEquals(
                VoteType.DOWN,
                voteService
                        .getUserPostVote(1L, 1L)
                        .orElseThrow()
                        .getType()
        );

        assertEquals(-1, voteService.getPostRating(1L));
    }
}