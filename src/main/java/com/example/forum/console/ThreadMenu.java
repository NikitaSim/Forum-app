package com.example.forum.console;

import java.util.List;

import com.example.forum.models.ForumThread;
import com.example.forum.service.ThreadService;

public class ThreadMenu {
    private final Session session;
    private final ConsoleUtils console;
    private final ThreadService service;

    public ThreadMenu(Session session, ConsoleUtils console, ThreadService service) {
        this.session = session;
        this.console = console;
        this.service = service;
    }

    public void show() {
        while (session.isAuthenticated()) {
            System.out.println("\n================================");
            System.out.println("              ТЕМЫ");
            System.out.println("================================");
            System.out.println("1. Показать все темы");
            System.out.println("2. Найти тему");
            System.out.println("3. Создать тему");
            System.out.println("4. Открыть тему");
            System.out.println("5. Добавить тег");
            System.out.println("6. Удалить тег");
            System.out.println("7. Удалить свою тему");
            System.out.println("0. Назад");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> listThreads();
                    case "2" -> search();
                    case "3" -> create();
                    case "4" -> showThread();
                    case "5" -> addTag();
                    case "6" -> removeTag();
                    case "7" -> delete();
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void listThreads() {
        List<ForumThread> threads = service.showThreads();

        System.out.println("\n--- Все темы ---");

        if (threads.isEmpty()) {
            System.out.println("Тем пока нет.");
        }

        for (ForumThread thread : threads) {
            printThread(thread);
        }

        console.pause();
    }

    private void search() {
        String query = console.readString("Поисковый запрос: ");
        List<ForumThread> threads = service.searchThreads(query);

        System.out.println("\n--- Результаты поиска ---");

        if (threads.isEmpty()) {
            System.out.println("Ничего не найдено.");
        }

        for (ForumThread thread : threads) {
            printThread(thread);
        }

        console.pause();
    }

    private void create() {
        Long id = console.readLong("ID новой темы: ");
        String title = console.readString("Название темы: ");

        ForumThread thread = new ForumThread(
                id,
                session.getCurrentUser(),
                title
        );

        service.createThread(thread);

        System.out.println("Тема создана.");
        console.pause();
    }

    private void showThread() {
        Long id = console.readLong("ID темы: ");

        ForumThread thread = service.getThread(id)
                .orElseThrow(() -> new IllegalArgumentException("Тема не найдена"));

        System.out.println("\n--- Тема ---");
        printThread(thread);

        System.out.println("Постов: " + thread.getPosts().size());
        console.pause();
    }

    private void addTag() {
        Long id = console.readLong("ID темы: ");
        String tag = console.readString("Тег: ");

        service.addTag(id, tag);

        System.out.println("Тег добавлен.");
        console.pause();
    }

    private void removeTag() {
        Long id = console.readLong("ID темы: ");
        String tag = console.readString("Тег: ");

        service.removeTag(id, tag);

        System.out.println("Тег удалён.");
        console.pause();
    }

    private void delete() {
        Long id = console.readLong("ID темы: ");

        ForumThread thread = service.getThread(id)
                .orElseThrow(() -> new IllegalArgumentException("Тема не найдена"));

        if (!thread.getAuthor().getId().equals(session.getCurrentUser().getId())) {
            throw new IllegalStateException("Можно удалять только свои темы.");
        }

        service.deleteThread(id);

        System.out.println("Тема удалена.");
        console.pause();
    }

    private void printThread(ForumThread thread) {
        System.out.println("--------------------------------");
        System.out.println("ID: " + thread.getId());
        System.out.println("Название: " + thread.getTitle());
        System.out.println("Автор: " + thread.getAuthor().getUsername());
        System.out.println("Теги: " + thread.getTags());
        System.out.println("Постов: " + thread.getPosts().size());
    }
}
