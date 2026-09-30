package com.example.forum.console;

import com.example.forum.models.User;
import com.example.forum.service.AuthService;

public class AuthMenu {
    private final AuthService authService;
    private final Session session;
    private final ConsoleUtils console;

    public AuthMenu(AuthService authService, Session session, ConsoleUtils console) {
        this.authService = authService;
        this.session = session;
        this.console = console;
    }

    public void show() {
        while (!session.isAuthenticated()) {
            System.out.println();
            System.out.println("================================");
            System.out.println("        FORUM APPLICATION");
            System.out.println("================================");
            System.out.println("1. Войти");
            System.out.println("2. Регистрация");
            System.out.println("0. Выход");
            System.out.println("================================");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> login();
                    case "2" -> register();
                    case "0" -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void login() {
        System.out.println("\n--- Вход ---");
        Long id = console.readLong("ID пользователя: ");
        String password = console.readString("Пароль: ");

        User user = authService.login(id, password);
        session.login(user);

        System.out.println("Добро пожаловать, " + user.getUsername() + "!");
    }

    private void register() {
        System.out.println("\n--- Регистрация ---");

        Long id = console.readLong("Введите ID пользователя: ");
        String username = console.readString("Введите username: ");
        String email = console.readString("Введите email: ");
        String password = console.readString("Введите пароль: ");

        User user = authService.register(id, username, email, password);
        session.login(user);

        System.out.println("Регистрация успешна!");
        System.out.println("Добро пожаловать, " + user.getUsername() + "!");
    }
}
