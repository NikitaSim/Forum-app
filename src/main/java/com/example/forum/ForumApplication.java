package com.example.forum;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.forum.console.ConsoleApp;

@SpringBootApplication
public class ForumApplication {

    public static void main(String[] args) {
        new ConsoleApp().start();
    }
}