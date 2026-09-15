package com.example.forum.console;

import java.util.Scanner;

public class ConsoleUtils {
    private final Scanner scanner;

    public ConsoleUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public Long readLong(String message) {
        while (true) {
            try {
                return Long.parseLong(readString(message));
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }

    public void pause() {
        System.out.println();
        System.out.println("Нажмите Enter...");
        scanner.nextLine();
    }
}
