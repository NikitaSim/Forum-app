package com.example.forum.api;

import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.Vote;
import com.example.forum.service.VoteService;
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
@RequestMapping("/api/votes")
@Tag(name = "6. Голоса", description = "Голосование за посты (UP / DOWN). Удаление — ADMIN и MODERATOR")
public class VoteController {
    private final VoteService votes;

    public VoteController(VoteService votes) {
        this.votes = votes;
    }

    @Operation(summary = "Проголосовать за пост", description = "Один пользователь может проголосовать за пост только один раз.")
    @ApiResponse(responseCode = "201", description = "Голос учтён")
    @ApiResponse(responseCode = "404", description = "Пользователь или пост не найден")
    @ApiResponse(responseCode = "409", description = "Пользователь уже голосовал за этот пост")
    @PostMapping
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody VoteRequest request) {
        log.info("Голос {} пользователя id={} за пост id={}", request.getType(), request.getUserId(), request.getPostId());
        Vote vote = votes.create(new Vote(null,
                new User(request.getUserId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null), request.getType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(VoteResponse.of(vote));
    }

    @Operation(summary = "Список голосов", description = "Без параметров возвращает все голоса. Фильтр: по посту.")
    @ApiResponse(responseCode = "200", description = "Список голосов (может быть пустым)")
    @GetMapping
    public List<VoteResponse> findAll(
            @Parameter(description = "Идентификатор поста", example = "1") @RequestParam(required = false) Long postId) {
        log.debug("Поиск голосов: postId={}", postId);
        return votes.findAll(postId).stream().map(VoteResponse::of).toList();
    }

    @Operation(summary = "Получить голос по ID")
    @ApiResponse(responseCode = "200", description = "Голос найден")
    @ApiResponse(responseCode = "404", description = "Голос не найден")
    @GetMapping("/{id}")
    public VoteResponse find(@Parameter(description = "Идентификатор голоса", example = "1") @PathVariable Long id) {
        log.debug("Получение голоса id={}", id);
        return VoteResponse.of(votes.find(id));
    }

    @Operation(summary = "Изменить тип голоса")
    @ApiResponse(responseCode = "200", description = "Голос обновлён")
    @ApiResponse(responseCode = "404", description = "Голос не найден")
    @PutMapping("/{id}")
    public VoteResponse update(@Parameter(description = "Идентификатор голоса", example = "1") @PathVariable Long id,
                               @Valid @RequestBody VoteUpdateRequest request) {
        log.info("Изменение голоса id={} на {}", id, request.getType());
        return VoteResponse.of(votes.update(id, new Vote(id, null, null, request.getType())));
    }

    @Operation(summary = "Удалить голос", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "204", description = "Голос удалён")
    @ApiResponse(responseCode = "404", description = "Голос не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор голоса", example = "1") @PathVariable Long id) {
        log.info("Удаление голоса id={}", id);
        votes.delete(id);
        return ResponseEntity.noContent().build();
    }
}
