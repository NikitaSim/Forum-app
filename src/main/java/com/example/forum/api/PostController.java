package com.example.forum.api;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.service.PostService;
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
@RequestMapping("/api/posts")
@Tag(name = "4. Посты", description = "Публикации в тредах. Удаление — ADMIN и MODERATOR")
public class PostController {
    private final PostService posts;

    public PostController(PostService posts) {
        this.posts = posts;
    }

    @Operation(summary = "Создать пост", description = "Доступно любому авторизованному пользователю.")
    @ApiResponse(responseCode = "201", description = "Пост создан")
    @ApiResponse(responseCode = "404", description = "Автор или тред не найден")
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        log.info("Создание поста автором id={} в треде id={}", request.getAuthorId(), request.getThreadId());
        Post post = posts.create(new Post(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new ForumThread(request.getThreadId(), null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.of(post));
    }

    @Operation(summary = "Список постов",
            description = "Без параметров возвращает все посты. Фильтры: по треду или по автору (threadId имеет приоритет).")
    @ApiResponse(responseCode = "200", description = "Список постов (может быть пустым)")
    @GetMapping
    public List<PostResponse> findAll(
            @Parameter(description = "Идентификатор треда", example = "1") @RequestParam(required = false) Long threadId,
            @Parameter(description = "Идентификатор автора", example = "1") @RequestParam(required = false) Long authorId) {
        log.debug("Поиск постов: threadId={}, authorId={}", threadId, authorId);
        return posts.findAll(threadId, authorId).stream().map(PostResponse::of).toList();
    }

    @Operation(summary = "Получить пост по ID")
    @ApiResponse(responseCode = "200", description = "Пост найден")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @GetMapping("/{id}")
    public PostResponse find(@Parameter(description = "Идентификатор поста", example = "1") @PathVariable Long id) {
        log.debug("Получение поста id={}", id);
        return PostResponse.of(posts.find(id));
    }

    @Operation(summary = "Изменить текст поста")
    @ApiResponse(responseCode = "200", description = "Пост обновлён")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @PutMapping("/{id}")
    public PostResponse update(@Parameter(description = "Идентификатор поста", example = "1") @PathVariable Long id,
                               @Valid @RequestBody ContentUpdateRequest request) {
        log.info("Обновление поста id={}", id);
        return PostResponse.of(posts.update(id, new Post(id, request.getContent(), null, null)));
    }

    @Operation(summary = "Удалить пост", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "204", description = "Пост удалён")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор поста", example = "1") @PathVariable Long id) {
        log.info("Удаление поста id={}", id);
        posts.delete(id);
        return ResponseEntity.noContent().build();
    }
}
