package com.example.forum.api;

import com.example.forum.models.UserRole;
import com.example.forum.models.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}


    @Schema(description = "Данные для регистрации нового пользователя (роль USER)")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @Schema(description = "Уникальное имя пользователя (до 32 символов)", example = "nikita")
        @NotBlank private String username;
        @Schema(description = "Адрес электронной почты", example = "nikita@mail.ru")
        @NotBlank @Email private String email;
        @Schema(description = "Пароль (от 6 до 72 символов)", example = "secret123")
        @NotBlank @Size(min = 6, max = 72) private String password;
    }

    @Schema(description = "Учётные данные для проверки входа")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Schema(description = "Имя пользователя", example = "nikita")
        @NotBlank private String username;
        @Schema(description = "Пароль", example = "secret123")
        @NotBlank private String password;
    }

    @Schema(description = "Информация о пользователе после регистрации или входа")
    public record AuthResponse(
            @Schema(description = "Идентификатор пользователя", example = "1") Long id,
            @Schema(description = "Имя пользователя", example = "nikita") String username,
            @Schema(description = "Адрес электронной почты", example = "nikita@mail.ru") String email,
            @Schema(description = "Роль пользователя") UserRole role) {
        public static AuthResponse of(com.example.forum.models.User user) {
            return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        }
    }

    @Schema(description = "Результат входа: JWT-токен и данные пользователя")
    public record LoginResponse(
            @Schema(description = "JWT-токен для заголовка Authorization: Bearer <token>") String token,
            @Schema(description = "Тип токена", example = "Bearer") String tokenType,
            @Schema(description = "Срок жизни токена в секундах", example = "3600") long expiresIn,
            @Schema(description = "Данные пользователя") AuthResponse user) {
    }

    @Schema(description = "Данные для создания пользователя администратором")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserRequest {
        @Schema(description = "Уникальное имя пользователя (до 32 символов)", example = "moderator1")
        @NotBlank private String username;
        @Schema(description = "Адрес электронной почты", example = "moderator1@mail.ru")
        @NotBlank @Email private String email;
        @Schema(description = "Пароль (не менее 6 символов)", example = "secret123")
        @NotBlank private String password;
        @Schema(description = "Роль пользователя. Если не указана, будет USER", example = "MODERATOR")
        private UserRole role;
    }
    @Schema(description = "Данные для обновления пользователя")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserUpdateRequest {
        @Schema(description = "Новое имя пользователя (до 32 символов)", example = "moderator1")
        @NotBlank private String username;
        @Schema(description = "Новый адрес электронной почты", example = "moderator1@mail.ru")
        @NotBlank @Email private String email;
        @Schema(description = "Новый пароль. Если не указан, пароль не меняется", example = "newSecret123")
        private String password;
        @Schema(description = "Новая роль. Если не указана, роль не меняется", example = "MODERATOR")
        private UserRole role;
    }
    @Schema(description = "Данные для создания или изменения треда")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ThreadRequest {
        @Schema(description = "Название треда", example = "Обсуждение Spring Security")
        @NotBlank private String title;
        @Schema(description = "Идентификатор автора треда", example = "1")
        @NotNull private Long authorId;
        @Schema(description = "Список тегов треда (необязательно)", example = "[\"java\", \"spring\"]")
        private List<String> tags;
    }
    @Schema(description = "Данные для создания поста")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PostRequest {
        @Schema(description = "Текст поста", example = "Как настроить роли в Spring Security?")
        @NotBlank private String content;
        @Schema(description = "Идентификатор автора поста", example = "1")
        @NotNull private Long authorId;
        @Schema(description = "Идентификатор треда, в котором создаётся пост", example = "1")
        @NotNull private Long threadId;
    }
    @Schema(description = "Данные для создания комментария")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CommentRequest {
        @Schema(description = "Текст комментария", example = "Используйте hasRole в SecurityFilterChain")
        @NotBlank private String content;
        @Schema(description = "Идентификатор автора комментария", example = "1")
        @NotNull private Long authorId;
        @Schema(description = "Идентификатор комментируемого поста", example = "1")
        @NotNull private Long postId;
    }
    @Schema(description = "Данные для создания голоса за пост")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VoteRequest {
        @Schema(description = "Идентификатор голосующего пользователя", example = "1")
        @NotNull private Long userId;
        @Schema(description = "Идентификатор поста", example = "1")
        @NotNull private Long postId;
        @Schema(description = "Тип голоса: UP (за) или DOWN (против)", example = "UP")
        @NotNull private VoteType type;
    }
    @Schema(description = "Новый текст поста или комментария")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ContentUpdateRequest {
        @Schema(description = "Новый текст", example = "Обновлённый текст")
        @NotBlank private String content;
    }

    @Schema(description = "Новый тип голоса")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VoteUpdateRequest {
        @Schema(description = "Тип голоса: UP (за) или DOWN (против)", example = "DOWN")
        @NotNull private VoteType type;
    }

    @Schema(description = "Тег для добавления к треду")
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TagRequest {
        @Schema(description = "Название тега (до 32 символов)", example = "java")
        @NotBlank private String name;
    }

    @Schema(description = "Пользователь (пароль в ответ не включается)")
    public record UserResponse(
            @Schema(description = "Идентификатор пользователя", example = "1") Long id,
            @Schema(description = "Имя пользователя", example = "nikita") String username,
            @Schema(description = "Адрес электронной почты", example = "nikita@mail.ru") String email,
            @Schema(description = "Роль пользователя") UserRole role) {
        public static UserResponse of(com.example.forum.models.User user) {
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        }
    }

    @Schema(description = "Тред форума")
    public record ThreadResponse(
            @Schema(description = "Идентификатор треда", example = "1") Long id,
            @Schema(description = "Название треда", example = "Обсуждение Spring Security") String title,
            @Schema(description = "Идентификатор автора", example = "1") Long authorId,
            @Schema(description = "Теги треда", example = "[\"java\", \"spring\"]") java.util.List<String> tags) {
        public static ThreadResponse of(com.example.forum.models.ForumThread thread) {
            return new ThreadResponse(thread.getId(), thread.getTitle(), thread.getAuthor().getId(),
                    thread.getTags().stream().map(com.example.forum.models.Tag::getName).toList());
        }
    }

    @Schema(description = "Пост в треде")
    public record PostResponse(
            @Schema(description = "Идентификатор поста", example = "1") Long id,
            @Schema(description = "Текст поста", example = "Как настроить роли?") String content,
            @Schema(description = "Идентификатор автора", example = "1") Long authorId,
            @Schema(description = "Идентификатор треда", example = "1") Long threadId) {
        public static PostResponse of(com.example.forum.models.Post post) {
            return new PostResponse(post.getId(), post.getContent(), post.getAuthor().getId(),
                    post.getThread().getId());
        }
    }

    @Schema(description = "Комментарий к посту")
    public record CommentResponse(
            @Schema(description = "Идентификатор комментария", example = "1") Long id,
            @Schema(description = "Текст комментария", example = "Спасибо!") String content,
            @Schema(description = "Идентификатор автора", example = "1") Long authorId,
            @Schema(description = "Идентификатор поста", example = "1") Long postId) {
        public static CommentResponse of(com.example.forum.models.Comment comment) {
            return new CommentResponse(comment.getId(), comment.getContent(), comment.getAuthor().getId(),
                    comment.getPost().getId());
        }
    }

    @Schema(description = "Голос за пост")
    public record VoteResponse(
            @Schema(description = "Идентификатор голоса", example = "1") Long id,
            @Schema(description = "Идентификатор пользователя", example = "1") Long userId,
            @Schema(description = "Идентификатор поста", example = "1") Long postId,
            @Schema(description = "Тип голоса: UP или DOWN") VoteType type) {
        public static VoteResponse of(com.example.forum.models.Vote vote) {
            return new VoteResponse(vote.getId(), vote.getUser().getId(), vote.getPost().getId(), vote.getType());
        }
    }
}
