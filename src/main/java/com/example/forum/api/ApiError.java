package com.example.forum.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Единый формат ответа об ошибке")
public record ApiError(
        @Schema(description = "Время возникновения ошибки", example = "2026-11-17T18:30:00") LocalDateTime timestamp,
        @Schema(description = "HTTP-статус", example = "404") int status,
        @Schema(description = "Название статуса", example = "Not Found") String error,
        @Schema(description = "Описание проблемы", example = "Пользователь с ID 15 не найден.") String message,
        @Schema(description = "Адрес запроса", example = "/api/users/15") String path,
        @Schema(description = "Рекомендация по устранению ошибки") String solution,
        @Schema(description = "Дополнительные сведения, например ошибки валидации полей") List<String> details
) {
}
