package com.example.forum.config;

import com.example.forum.api.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Настройка Swagger / OpenAPI: описание API, схема авторизации JWT (Bearer)
 * и общие коды ошибок для всех методов.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Forum API",
                version = "1.0",
                description = "REST API форума: пользователи, треды, посты, комментарии и голосования. "
                        + "Все методы, кроме /api/auth/**, требуют JWT-токена: получите его в POST /api/auth/login и нажмите Authorize. "
                        + "Права зависят от роли: ADMIN, MODERATOR, USER."),
        security = @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH))
@SecurityScheme(
        name = OpenApiConfig.BASIC_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT-токен из POST /api/auth/login (вводится без слова Bearer). "
                + "Администратор по умолчанию: admin / admin123.")
public class OpenApiConfig {
    public static final String BASIC_AUTH = "bearerAuth";

    /** Добавляет к каждой операции описание типовых ошибок 400, 401, 403 и 500 (формат ApiError). */
    @Bean
    OpenApiCustomizer commonErrorResponsesCustomizer() {
        return openApi -> {
            registerApiErrorSchema(openApi);
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((path, item) -> {
                boolean publicPath = path.startsWith("/api/auth");
                for (Operation operation : item.readOperations()) {
                    ApiResponses responses = operation.getResponses();
                    if (responses == null) {
                        responses = new ApiResponses();
                        operation.setResponses(responses);
                    }
                    addIfAbsent(responses, "400", "Некорректный запрос или ошибка валидации данных");
                    if (!publicPath) {
                        addIfAbsent(responses, "401", "Не выполнена аутентификация (токен отсутствует, неверен или просрочен)");
                        addIfAbsent(responses, "403", "Недостаточно прав: роль пользователя не позволяет выполнить операцию");
                    }
                    addIfAbsent(responses, "500", "Внутренняя ошибка сервера");
                    attachErrorBody(responses);
                }
            });
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerApiErrorSchema(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ApiError.class);
        schemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));
    }

    private void addIfAbsent(ApiResponses responses, String code, String description) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, new ApiResponse().description(description));
        }
    }

    /** Для всех ответов с кодом 4xx/5xx без тела указывает схему ApiError. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void attachErrorBody(ApiResponses responses) {
        responses.forEach((code, response) -> {
            boolean error = code.startsWith("4") || code.startsWith("5");
            if (error && (response.getContent() == null || response.getContent().isEmpty())) {
                Schema ref = new Schema().$ref("#/components/schemas/ApiError");
                response.setContent(new Content()
                        .addMediaType("application/json", new MediaType().schema(ref)));
            }
        });
    }
}
