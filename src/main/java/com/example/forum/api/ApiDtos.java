package com.example.forum.api;

import com.example.forum.models.UserRole;
import com.example.forum.models.VoteType;
import jakarta.validation.constraints.*;
import lombok.*;

public final class ApiDtos {
    private ApiDtos() {}


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank private String username;
        @NotBlank @Email private String email;
        @NotBlank @Size(min = 6, max = 72) private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    public record AuthResponse(Long id, String username, String email, UserRole role) {
        public static AuthResponse of(com.example.forum.models.User user) {
            return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserRequest {
        @NotBlank private String username;
        @NotBlank @Email private String email;
        @NotBlank private String password;
        private UserRole role;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserUpdateRequest {
        @NotBlank private String username;
        @NotBlank @Email private String email;
        private String password;
        private UserRole role;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ThreadRequest {
        @NotBlank private String title;
        @NotNull private Long authorId;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PostRequest {
        @NotBlank private String content;
        @NotNull private Long authorId;
        @NotNull private Long threadId;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CommentRequest {
        @NotBlank private String content;
        @NotNull private Long authorId;
        @NotNull private Long postId;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VoteRequest {
        @NotNull private Long userId;
        @NotNull private Long postId;
        @NotNull private VoteType type;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ContentUpdateRequest {
        @NotBlank private String content;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VoteUpdateRequest {
        @NotNull private VoteType type;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TagRequest {
        @NotBlank private String name;
    }
}
