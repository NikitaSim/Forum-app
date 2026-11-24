package com.example.forum.api;

import com.example.forum.models.User;
import com.example.forum.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "2. Пользователи", description = "Управление пользователями. Изменение — только ADMIN, просмотр — ADMIN и MODERATOR")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @Operation(summary = "Создать пользователя", description = "Доступно роли ADMIN. Позволяет задать роль пользователя.")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @ApiResponse(responseCode = "409", description = "Имя пользователя или email уже заняты")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        log.info("Создание пользователя '{}' с ролью {}", request.getUsername(), request.getRole());
        User user = users.create(new User(null, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user));
    }

    @Operation(summary = "Список пользователей", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "200", description = "Список пользователей (может быть пустым)")
    @GetMapping
    public List<UserResponse> findAll() {
        log.debug("Получение списка пользователей");
        return users.findAll().stream().map(UserResponse::of).toList();
    }

    @Operation(summary = "Получить пользователя по ID", description = "Доступно ролям ADMIN и MODERATOR.")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @GetMapping("/{id}")
    public UserResponse find(@Parameter(description = "Идентификатор пользователя", example = "1") @PathVariable Long id) {
        log.debug("Получение пользователя id={}", id);
        return UserResponse.of(users.find(id));
    }

    @Operation(summary = "Обновить пользователя", description = "Доступно роли ADMIN. Пустой пароль или роль не изменяются.")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "409", description = "Имя пользователя или email уже заняты")
    @PutMapping("/{id}")
    public UserResponse update(@Parameter(description = "Идентификатор пользователя", example = "1") @PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequest request) {
        log.info("Обновление пользователя id={}", id);
        User input = new User(id, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole());
        return UserResponse.of(users.update(id, input));
    }

    @Operation(summary = "Удалить пользователя",
            description = "Доступно роли ADMIN. Нельзя удалить пользователя, у которого есть треды, посты или комментарии.")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "409", description = "У пользователя есть связанные записи")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Идентификатор пользователя", example = "1") @PathVariable Long id) {
        log.info("Удаление пользователя id={}", id);
        users.delete(id);
        return ResponseEntity.noContent().build();
    }
}
