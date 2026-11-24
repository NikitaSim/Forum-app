package com.example.forum.api;

import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.service.CommentService;
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
@RequestMapping("/api/comments")
@Tag(name = "5. Комментарии", description = "Комментарии к постам. Удаление — ADMIN и MODERATOR")
public class CommentController {
    private final CommentService comments;

    public CommentController(CommentService comments) {
        this.comments = comments;
    }

    @Operation(summary = "Создать комментарий", description = "Доступно любому авторизованному пользователю.")
    @ApiResponse(responseCode = "201", description = "Комментарий создан")
    @ApiResponse(responseCode = "404", description = "Автор или пост не найден")
    @PostMapping
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentRequest request) {
        log.info("Создание комментария автором id={} к посту id={}", request.getAuthorId(), request.getPostId());
        Comment comment = comments.create(new Comment(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @Operation(summary = "Список комментариев",
            description = "Без параметров возвращает все комментарии. Фильтры: по посту или по автору (postId имеет приоритет).")
    @ApiResponse(responseCode = "200", description = "Список комментариев (может быть пустым)")
    @GetMapping
    public List<CommentResponse> findAll(
            @Parameter(description = "Идентификатор поста", example = "1") @RequestParam(required = false) Long postId,
            @Parameter(description = "Идентификатор автора", example = "1") @RequestParam(required = false) Long authorId) {
        log.debug("Поиск комментариев: postId={}, authorId={}", postId, authorId);
        return comments.findAll(postId, authorId).stream().map(CommentResponse::of).toList();
    }

    @Operation(summary = "Получить комментарий по ID")
    @ApiResponse(responseCode = "200", description = "Комментарий найден")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    @GetMapping("/{id}")
    public CommentResponse find(@Parameter(description = "Идентификатор комментария", example = "1") @PathVariable Long id) {
        log.debug("Получение комментария id={}", id);
        return CommentResponse.of(comments.find(id));
    }

    @Operation(summary = "Изменить текст комментария")
    @ApiResponse(responseCode = "200", description = "Комментарий обновлён")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    @PutMapping("/{id}")
    public CommentResponse update(@Parameter(description = "Идентификатор комментария", example = "1") @PathVariable Long id,
                                  @Valid @RequestBody ContentUpdateRequest request) {
        log.info("Обновление комментария id={}", id);
        return CommentResponse.of(comments.update(id, new Comment(id, request.getContent(), null, null)));
    }

    @Operation(summary = "Удалить комментарий", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "204", description = "Комментарий удалён")
    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор комментария", example = "1") @PathVariable Long id) {
        log.info("Удаление комментария id={}", id);
        comments.delete(id);
        return ResponseEntity.noContent().build();
    }
}
