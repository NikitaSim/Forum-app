package com.example.forum.security;

import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final String SECRET = "test-secret-key-test-secret-key-12345";
    private final User user = new User(1L, "nikita", "n@mail.ru", "hash", UserRole.MODERATOR);

    @Test
    void generatedTokenContainsUsername() {
        JwtService service = new JwtService(SECRET, 60);

        String token = service.generateToken(user);

        assertEquals("nikita", service.extractUsername(token).orElseThrow());
    }

    @Test
    void rejectsTamperedToken() {
        JwtService service = new JwtService(SECRET, 60);

        assertTrue(service.extractUsername(service.generateToken(user) + "x").isEmpty());
        assertTrue(service.extractUsername("not-a-token").isEmpty());
    }

    @Test
    void rejectsTokenSignedWithOtherSecret() {
        String token = new JwtService("another-secret-key-another-secret-key-1", 60).generateToken(user);

        assertTrue(new JwtService(SECRET, 60).extractUsername(token).isEmpty());
    }

    @Test
    void rejectsExpiredToken() {
        JwtService service = new JwtService(SECRET, -1);

        assertTrue(service.extractUsername(service.generateToken(user)).isEmpty());
    }

    @Test
    void rejectsShortSecret() {
        assertThrows(IllegalStateException.class, () -> new JwtService("short", 60));
    }
}
