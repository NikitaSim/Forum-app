package com.example.forum.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.example.forum.repository.VoteRepository;

class VoteServiceConcurrencyTest {

    private VoteService voteService;
    private VoteRepository voteRepository;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        var userRepository = new InMemoryUserRepository();
        var postRepository = new InMemoryPostRepository();
        voteRepository = new InMemoryVoteRepository();

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
    void concurrentVotes_shouldCreateOnlyOneVote() throws Exception {
        int threadCount = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            long voteId = i + 1L;

            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();

                    Vote vote = new Vote(
                            voteId,
                            user,
                            post,
                            VoteType.UP
                    );

                    voteService.createVote(vote);

                } catch (IllegalStateException e) {
                    // Ожидаемое поведение:
                    // только один поток может создать голос.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return null;
            }));
        }

        // Одновременно запускаем все потоки
        startLatch.countDown();

        // Ждём завершения всех задач
        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        assertEquals(
                1,
                voteRepository.findAllByPostId(post.getId()).size()
        );
    }
}