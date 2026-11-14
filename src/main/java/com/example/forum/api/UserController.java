package com.example.forum.api;

import com.example.forum.models.User;
import com.example.forum.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = users.create(new User(null, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user));
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return users.findAll().stream().map(UserResponse::of).toList();
    }

    @GetMapping("/{id}")
    public UserResponse find(@PathVariable Long id) {
        return UserResponse.of(users.find(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        User input = new User(id, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole());
        return UserResponse.of(users.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }
}
