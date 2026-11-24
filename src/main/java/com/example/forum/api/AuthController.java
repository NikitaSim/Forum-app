package com.example.forum.api;

import com.example.forum.models.User;
import com.example.forum.security.JwtService;
import com.example.forum.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.forum.api.ApiDtos.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Авторизация", description = "Регистрация и получение JWT-токена. Доступно без авторизации")
@SecurityRequirements
public class AuthController {
    private final AuthService auth;
    private final JwtService jwtService;

    public AuthController(AuthService auth, JwtService jwtService) {
        this.auth = auth;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Регистрация пользователя",
            description = "Создаёт нового пользователя с ролью USER. Доступно всем.")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован")
    @ApiResponse(responseCode = "409", description = "Имя пользователя или email уже заняты")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Запрос на регистрацию пользователя '{}'", request.getUsername());
        User user = auth.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.of(user));
    }

    @Operation(summary = "Вход (получение JWT-токена)",
            description = "Проверяет имя пользователя и пароль и возвращает JWT-токен. "
                    + "Для остальных методов передавайте его в заголовке Authorization: Bearer <token>.")
    @ApiResponse(responseCode = "200", description = "Вход выполнен, токен выдан")
    @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Запрос на вход пользователя '{}'", request.getUsername());
        User user = auth.login(request.getUsername(), request.getPassword());
        return new LoginResponse(jwtService.generateToken(user), "Bearer",
                jwtService.getExpiresInSeconds(), AuthResponse.of(user));
    }
}
