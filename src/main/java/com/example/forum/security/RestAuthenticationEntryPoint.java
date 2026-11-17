package com.example.forum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Возвращает 401 в формате ApiError, если запрос не аутентифицирован. */
@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        log.warn("401 Unauthorized: {} {} ({})", request.getMethod(), request.getRequestURI(),
                exception.getMessage());
        JsonErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED,
                "Требуется аутентификация: токен отсутствует, неверен или просрочен.",
                "Получите токен через POST /api/auth/login и передайте его в заголовке Authorization: Bearer <token>.");
    }
}
