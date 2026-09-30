package com.example.forum.console;

import java.util.List;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.service.PostService;
import com.example.forum.service.ThreadService;

public class PostMenu {
    private final Session session;
    private final ConsoleUtils console;
    private final PostService service;
    private final ThreadService threadService;

    public PostMenu(
            Session session,
            ConsoleUtils console,
            PostService service,
            ThreadService threadService
    ) {
        this.session = session;
        this.console = console;
        this.service = service;
        this.threadService = threadService;
    }

    public void show() {
        while (session.isAuthenticated()) {
            System.out.println("\n================================");
            System.out.println("              ПОСТЫ");
            System.out.println("================================");
            System.out.println("1. Показать мои посты");
            System.out.println("2. Показать посты темы");
            System.out.println("3. Создать пост");
            System.out.println("4. Найти пост");
            System.out.println("5. Открыть пост");
            System.out.println("6. Удалить свой пост");
            System.out.println("0. Назад");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> myPosts();
                    case "2" -> threadPosts();
                    case "3" -> create();
                    case "4" -> search();
                    case "5" -> showPost();
                    case "6" -> delete();
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void myPosts() {
        List<Post> posts = service.showAuthorPosts(
                session.getCurrentUser().getId()
        );

        printPosts(posts);
        console.pause();
    }

    private void threadPosts() {
        Long threadId = console.readLong("ID темы: ");

        List<Post> posts = service.showThreadPosts(threadId);

        printPosts(posts);
        console.pause();
    }

    private void create() {
        Long threadId = console.readLong("ID темы: ");

        ForumThread thread = threadService.getThread(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Тема не найдена"));

        Long id = console.readLong("ID нового поста: ");
        String content = console.readString("Текст поста: ");

        Post post = new Post(
                id,
                content,
                session.getCurrentUser(),
                thread
        );

        service.createPost(post);

        System.out.println("Пост создан.");
        console.pause();
    }

    private void search() {
        String query = console.readString("Поисковый запрос: ");

        List<Post> posts = service.searchPosts(query);

        printPosts(posts);
        console.pause();
    }

    private void showPost() {
        Long id = console.readLong("ID поста: ");

        Post post = service.getPost(id)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        System.out.println("\n--- Пост ---");
        printPost(post);
        System.out.println("Комментариев: " + post.getComments().size());
        System.out.println("Рейтинг: " + service.getPostRating(id));

        console.pause();
    }

    private void delete() {
        Long id = console.readLong("ID поста: ");

        Post post = service.getPost(id)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        if (!post.getAuthor().getId().equals(session.getCurrentUser().getId())) {
            throw new IllegalStateException("Можно удалять только свои посты.");
        }

        service.deletePost(id);

        System.out.println("Пост удалён.");
        console.pause();
    }

    private void printPosts(List<Post> posts) {
        System.out.println("\n--- Посты ---");

        if (posts.isEmpty()) {
            System.out.println("Постов нет.");
            return;
        }

        for (Post post : posts) {
            printPost(post);
        }
    }

    private void printPost(Post post) {
        System.out.println("--------------------------------");
        System.out.println("ID: " + post.getId());
        System.out.println("Автор: " + post.getAuthor().getUsername());
        System.out.println("Тема: " + post.getThread().getTitle());
        System.out.println("Текст: " + post.getContent());
    }
}
