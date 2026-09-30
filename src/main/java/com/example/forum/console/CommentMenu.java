package com.example.forum.console;

import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.service.CommentService;
import com.example.forum.service.PostService;

import java.util.List;

public class CommentMenu {
    private final Session session;
    private final ConsoleUtils console;
    private final CommentService service;
    private final PostService postService;

    public CommentMenu(
            Session session,
            ConsoleUtils console,
            CommentService service,
            PostService postService
    ) {
        this.session = session;
        this.console = console;
        this.service = service;
        this.postService = postService;
    }

    public void show() {
        while (session.isAuthenticated()) {
            System.out.println("\n================================");
            System.out.println("          КОММЕНТАРИИ");
            System.out.println("================================");
            System.out.println("1. Показать мои комментарии");
            System.out.println("2. Показать комментарии поста");
            System.out.println("3. Добавить комментарий");
            System.out.println("4. Открыть комментарий");
            System.out.println("5. Удалить свой комментарий");
            System.out.println("0. Назад");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> myComments();
                    case "2" -> postComments();
                    case "3" -> create();
                    case "4" -> showComment();
                    case "5" -> delete();
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void myComments() {
        List<Comment> comments = service.showAuthorComments(
                session.getCurrentUser().getId()
        );

        printComments(comments);
        console.pause();
    }

    private void postComments() {
        Long postId = console.readLong("ID поста: ");

        List<Comment> comments = service.showPostComments(postId);

        printComments(comments);
        console.pause();
    }

    private void create() {
        Long postId = console.readLong("ID поста: ");

        Post post = postService.getPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        Long id = console.readLong("ID нового комментария: ");
        String content = console.readString("Текст комментария: ");

        Comment comment = new Comment(
                id,
                content,
                session.getCurrentUser(),
                post
        );

        service.createComment(comment);

        System.out.println("Комментарий добавлен.");
        console.pause();
    }

    private void showComment() {
        Long id = console.readLong("ID комментария: ");

        Comment comment = service.getComment(id)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));

        printComment(comment);
        console.pause();
    }

    private void delete() {
        Long id = console.readLong("ID комментария: ");

        Comment comment = service.getComment(id)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));

        if (!comment.getAuthor().getId().equals(session.getCurrentUser().getId())) {
            throw new IllegalStateException("Можно удалять только свои комментарии.");
        }

        service.deleteComment(id);

        System.out.println("Комментарий удалён.");
        console.pause();
    }

    private void printComments(List<Comment> comments) {
        System.out.println("\n--- Комментарии ---");

        if (comments.isEmpty()) {
            System.out.println("Комментариев нет.");
            return;
        }

        for (Comment comment : comments) {
            printComment(comment);
        }
    }

    private void printComment(Comment comment) {
        System.out.println("--------------------------------");
        System.out.println("ID: " + comment.getId());
        System.out.println("Автор: " + comment.getAuthor().getUsername());
        System.out.println("Пост: " + comment.getPost().getId());
        System.out.println("Текст: " + comment.getContent());
    }
}
