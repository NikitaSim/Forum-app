package com.example.forum.console;

import com.example.forum.service.CommentService;
import com.example.forum.service.PostService;
import com.example.forum.service.ThreadService;
import com.example.forum.service.UserService;
import com.example.forum.service.VoteService;


public class MainMenu {
    private final Session session;
    private final ConsoleUtils console;
    private final UserService userService;
    private final ThreadService threadService;
    private final PostService postService;
    private final CommentService commentService;
    private final VoteService voteService;

    public MainMenu(
            Session session,
            ConsoleUtils console,
            UserService userService,
            ThreadService threadService,
            PostService postService,
            CommentService commentService,
            VoteService voteService
    ) {
        this.session = session;
        this.console = console;
        this.userService = userService;
        this.threadService = threadService;
        this.postService = postService;
        this.commentService = commentService;
        this.voteService = voteService;
    }

    public void show() {
        ThreadMenu threadMenu = new ThreadMenu(session, console, threadService);
        PostMenu postMenu = new PostMenu(session, console, postService, threadService);
        CommentMenu commentMenu = new CommentMenu(session, console, commentService, postService);
        VoteMenu voteMenu = new VoteMenu(session, console, voteService, postService);

        while (session.isAuthenticated()) {
            var user = session.getCurrentUser();

            System.out.println("\n================================");
            System.out.println("              FORUM");
            System.out.println("================================");
            System.out.println("Пользователь: " + user.getUsername());
            System.out.println("Роль: " + user.getRole());
            System.out.println("================================");
            System.out.println("1. Мой профиль");
            System.out.println("2. Темы");
            System.out.println("3. Посты");
            System.out.println("4. Комментарии");
            System.out.println("5. Голосования");
            System.out.println("0. Выйти из аккаунта");
            System.out.println("================================");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> showProfile();
                    case "2" -> threadMenu.show();
                    case "3" -> postMenu.show();
                    case "4" -> commentMenu.show();
                    case "5" -> voteMenu.show();
                    case "0" -> logout();
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void showProfile() {
        var user = session.getCurrentUser();

        System.out.println("\n--- Мой профиль ---");
        System.out.println("ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getRole());
        System.out.println("Тем: " + user.getThreads().size());
        System.out.println("Постов: " + user.getPosts().size());
        System.out.println("Комментариев: " + user.getComments().size());

        console.pause();
    }

    private void logout() {
        String answer = console.readString("Выйти из аккаунта? (y/n): ");

        if (answer.equalsIgnoreCase("y")) {
            session.logout();
            System.out.println("Вы вышли из аккаунта.");
        }
    }
}
