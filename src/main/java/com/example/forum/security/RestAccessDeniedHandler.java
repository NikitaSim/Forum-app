package com.example.forum.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Возвращает 403 в формате ApiError, если у роли пользователя недостаточно прав. */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication == null ? "anonymous" : authentication.getName();
        log.warn("403 Forbidden: пользователь '{}' пытался выполнить {} {}", user,
                request.getMethod(), request.getRequestURI());
        JsonErrorWriter.write(request, response, HttpStatus.FORBIDDEN,
                "Недостаточно прав для выполнения этой операции.",
                "Войдите под пользователем с подходящей ролью (например, ADMIN или MODERATOR).");
    }
}
