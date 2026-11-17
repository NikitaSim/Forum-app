package com.example.forum.security;

import com.example.forum.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

/** Выпуск и проверка JWT-токенов (HS256). В токене хранятся имя пользователя и роль. */
@Component
public class JwtService {
    private final SecretKey key;
    private final Duration lifetime;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.expiration-minutes}") long expirationMinutes) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT-секрет должен содержать не менее 32 символов.");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.lifetime = Duration.ofMinutes(expirationMinutes);
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + lifetime.toMillis()))
                .signWith(key)
                .compact();
    }

    public Optional<String> extractUsername(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long getExpiresInSeconds() {
        return lifetime.toSeconds();
    }
}
