package com.example.forum.console;

import java.util.Scanner;

import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.MemoryRepository.InMemoryCommentRepository;
import com.example.forum.repository.MemoryRepository.InMemoryPostRepository;
import com.example.forum.repository.MemoryRepository.InMemoryThreadRepository;
import com.example.forum.repository.MemoryRepository.InMemoryUserRepository;
import com.example.forum.repository.MemoryRepository.InMemoryVoteRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import com.example.forum.repository.VoteRepository;
import com.example.forum.service.AuthService;
import com.example.forum.service.CommentService;
import com.example.forum.service.PostService;
import com.example.forum.service.ThreadService;
import com.example.forum.service.UserService;
import com.example.forum.service.VoteService;


public class ConsoleApp {

    public void start() {
        Scanner scanner = new Scanner(System.in);
        ConsoleUtils console = new ConsoleUtils(scanner);

        UserRepository userRepository = new InMemoryUserRepository();
        ThreadRepository threadRepository = new InMemoryThreadRepository();
        PostRepository postRepository = new InMemoryPostRepository();
        CommentRepository commentRepository = new InMemoryCommentRepository();
        VoteRepository voteRepository = new InMemoryVoteRepository();

        UserService userService = new UserService(userRepository);
        AuthService authService = new AuthService(userService, userRepository);

        ThreadService threadService =
                new ThreadService(threadRepository, userRepository);

        PostService postService =
                new PostService(postRepository, userRepository, threadRepository);

        CommentService commentService =
                new CommentService(commentRepository, userRepository, postRepository);

        VoteService voteService =
                new VoteService(voteRepository, userRepository, postRepository);

        Session session = new Session();

        AuthMenu authMenu = new AuthMenu(authService, session, console);

        MainMenu mainMenu = new MainMenu(
                session,
                console,
                userService,
                threadService,
                postService,
                commentService,
                voteService
        );

        while (true) {
            authMenu.show();

            if (!session.isAuthenticated()) {
                break;
            }

            mainMenu.show();
        }

        scanner.close();
    }
}
