package com.example.forum.console;

import com.example.forum.models.Post;
import com.example.forum.models.Vote;
import com.example.forum.models.VoteType;
import com.example.forum.service.PostService;
import com.example.forum.service.VoteService;

import java.util.Optional;

public class VoteMenu {
    private final Session session;
    private final ConsoleUtils console;
    private final VoteService service;
    private final PostService postService;

    public VoteMenu(
            Session session,
            ConsoleUtils console,
            VoteService service,
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
            System.out.println("          ГОЛОСОВАНИЯ");
            System.out.println("================================");
            System.out.println("1. Поставить голос");
            System.out.println("2. Изменить голос");
            System.out.println("3. Удалить голос");
            System.out.println("4. Посмотреть мой голос");
            System.out.println("5. Посмотреть рейтинг поста");
            System.out.println("0. Назад");

            String choice = console.readString("Выберите действие: ");

            try {
                switch (choice) {
                    case "1" -> create();
                    case "2" -> change();
                    case "3" -> delete();
                    case "4" -> myVote();
                    case "5" -> rating();
                    case "0" -> { return; }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Ошибка: " + e.getMessage());
                console.pause();
            }
        }
    }

    private void create() {
        Long postId = console.readLong("ID поста: ");

        Post post = postService.getPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        VoteType type = readVoteType();

        Long voteId = console.readLong("ID голоса: ");

        Vote vote = new Vote(
                voteId,
                session.getCurrentUser(),
                post,
                type
        );

        service.createVote(vote);

        System.out.println("Голос принят.");
        console.pause();
    }

    private void change() {
        Long postId = console.readLong("ID поста: ");
        VoteType type = readVoteType();

        service.changeVote(
                session.getCurrentUser().getId(),
                postId,
                type
        );

        System.out.println("Голос изменён.");
        console.pause();
    }

    private void delete() {
        Long postId = console.readLong("ID поста: ");

        Vote vote = service.getUserPostVote(
                session.getCurrentUser().getId(),
                postId
        ).orElseThrow(() -> new IllegalArgumentException(
                "У вас нет голоса за этот пост"
        ));

        service.deleteVote(vote.getId());

        System.out.println("Голос удалён.");
        console.pause();
    }

    private void myVote() {
        Long postId = console.readLong("ID поста: ");

        Optional<Vote> vote = service.getUserPostVote(
                session.getCurrentUser().getId(),
                postId
        );

        if (vote.isEmpty()) {
            System.out.println("Вы ещё не голосовали за этот пост.");
        } else {
            System.out.println("Ваш голос: " + vote.get().getType());
        }

        console.pause();
    }

    private void rating() {
        Long postId = console.readLong("ID поста: ");

        int rating = service.getPostRating(postId);
        long up = service.countUpVotes(postId);
        long down = service.countDownVotes(postId);

        System.out.println("\n--- Рейтинг ---");
        System.out.println("Рейтинг: " + rating);
        System.out.println("UP: " + up);
        System.out.println("DOWN: " + down);

        console.pause();
    }

    private VoteType readVoteType() {
        while (true) {
            String value = console.readString(
                    "Тип голоса (1 - UP, 2 - DOWN): "
            );

            switch (value) {
                case "1" -> { return VoteType.UP; }
                case "2" -> { return VoteType.DOWN; }
                default -> System.out.println("Введите 1 или 2.");
            }
        }
    }
}
