package com.example.forum.api;

import com.example.forum.api.ApiDtos.TagRequest;
import com.example.forum.api.ApiDtos.ThreadRequest;
import com.example.forum.api.ApiDtos.ThreadResponse;
import com.example.forum.models.ForumThread;
import com.example.forum.models.User;
import com.example.forum.service.ThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@Slf4j
@RestController
@RequestMapping("/api/threads")
@Tag(name = "3. Треды", description = "Тематические обсуждения и их теги. Удаление — ADMIN и MODERATOR")
public class ThreadController {
    private final ThreadService threads;

    public ThreadController(ThreadService threads) {
        this.threads = threads;
    }

    // @PostMapping
    // public ResponseEntity<ThreadResponse> create(@Valid @RequestBody ThreadRequest request) {
    //     ForumThread thread = threads.create(new ForumThread(null,
    //             new User(request.getAuthorId(), null, null, null, null), request.getTitle()));
    //     return ResponseEntity.status(HttpStatus.CREATED).body(ThreadResponse.of(thread));
    // }

    @Operation(summary = "Создать тред", description = "Доступно любому авторизованному пользователю. Теги создаются автоматически.")
    @ApiResponse(responseCode = "201", description = "Тред создан")
    @ApiResponse(responseCode = "404", description = "Автор не найден")
    @PostMapping
    public ResponseEntity<ThreadResponse> create(@Valid @RequestBody ThreadRequest request) {
        log.info("Создание треда '{}' автором id={}", request.getTitle(), request.getAuthorId());
        ForumThread thread = threads.create(
            new ForumThread(null,
                    new User(request.getAuthorId(), null, null, null, null),
                    request.getTitle()),
            request.getTags()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ThreadResponse.of(thread));
    }

    @Operation(summary = "Список тредов",
            description = "Без параметров возвращает все треды. Фильтры: по части названия или по автору (title имеет приоритет).")
    @ApiResponse(responseCode = "200", description = "Список тредов (может быть пустым)")
    @GetMapping
    public List<ThreadResponse> findAll(
            @Parameter(description = "Часть названия треда (без учёта регистра)", example = "spring")
            @RequestParam(required = false) String title,
            @Parameter(description = "Идентификатор автора", example = "1")
            @RequestParam(required = false) Long authorId) {
        log.debug("Поиск тредов: title={}, authorId={}", title, authorId);
        return threads.findAll(title, authorId).stream().map(ThreadResponse::of).toList();
    }

    @Operation(summary = "Получить тред по ID")
    @ApiResponse(responseCode = "200", description = "Тред найден")
    @ApiResponse(responseCode = "404", description = "Тред не найден")
    @GetMapping("/{id}")
    public ThreadResponse find(@Parameter(description = "Идентификатор треда", example = "1") @PathVariable Long id) {
        log.debug("Получение треда id={}", id);
        return ThreadResponse.of(threads.find(id));
    }

    @Operation(summary = "Обновить тред", description = "Меняет название и автора треда.")
    @ApiResponse(responseCode = "200", description = "Тред обновлён")
    @ApiResponse(responseCode = "404", description = "Тред или автор не найден")
    @PutMapping("/{id}")
    public ThreadResponse update(@Parameter(description = "Идентификатор треда", example = "1") @PathVariable Long id,
                                 @Valid @RequestBody ThreadRequest request) {
        log.info("Обновление треда id={}", id);
        ForumThread input = new ForumThread(id,
                new User(request.getAuthorId(), null, null, null, null), request.getTitle());
        return ThreadResponse.of(threads.update(id, input));
    }

    @Operation(summary = "Удалить тред", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "204", description = "Тред удалён")
    @ApiResponse(responseCode = "404", description = "Тред не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор треда", example = "1") @PathVariable Long id) {
        log.info("Удаление треда id={}", id);
        threads.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить тег к треду")
    @ApiResponse(responseCode = "200", description = "Тег добавлен")
    @ApiResponse(responseCode = "404", description = "Тред не найден")
    @PostMapping("/{id}/tags")
    public ThreadResponse addTag(@Parameter(description = "Идентификатор треда", example = "1") @PathVariable Long id,
                                 @Valid @RequestBody TagRequest request) {
        log.info("Добавление тега '{}' к треду id={}", request.getName(), id);
        return ThreadResponse.of(threads.addTag(id, request.getName()));
    }

    @Operation(summary = "Удалить тег из треда")
    @ApiResponse(responseCode = "200", description = "Тег удалён (если его не было, тред не меняется)")
    @ApiResponse(responseCode = "404", description = "Тред не найден")
    @DeleteMapping("/{id}/tags")
    public ThreadResponse removeTag(@Parameter(description = "Идентификатор треда", example = "1") @PathVariable Long id,
                                    @Parameter(description = "Название тега", example = "java", required = true)
                                    @RequestParam String name) {
        log.info("Удаление тега '{}' из треда id={}", name, id);
        return ThreadResponse.of(threads.removeTag(id, name));
    }
}
