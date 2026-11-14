package com.example.forum.api;

import com.example.forum.api.ApiDtos.TagRequest;
import com.example.forum.api.ApiDtos.ThreadRequest;
import com.example.forum.api.ApiDtos.ThreadResponse;
import com.example.forum.models.ForumThread;
import com.example.forum.models.User;
import com.example.forum.service.ThreadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api/threads")
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

    @PostMapping
    public ResponseEntity<ThreadResponse> create(@Valid @RequestBody ThreadRequest request) {
        ForumThread thread = threads.create(
            new ForumThread(null,
                    new User(request.getAuthorId(), null, null, null, null),
                    request.getTitle()),
            request.getTags()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ThreadResponse.of(thread));
    }

    @GetMapping
    public List<ThreadResponse> findAll(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) Long authorId) {
        return threads.findAll(title, authorId).stream().map(ThreadResponse::of).toList();
    }

    @GetMapping("/{id}")
    public ThreadResponse find(@PathVariable Long id) {
        return ThreadResponse.of(threads.find(id));
    }

    @PutMapping("/{id}")
    public ThreadResponse update(@PathVariable Long id, @Valid @RequestBody ThreadRequest request) {
        ForumThread input = new ForumThread(id,
                new User(request.getAuthorId(), null, null, null, null), request.getTitle());
        return ThreadResponse.of(threads.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        threads.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tags")
    public ThreadResponse addTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        return ThreadResponse.of(threads.addTag(id, request.getName()));
    }

    @DeleteMapping("/{id}/tags")
    public ThreadResponse removeTag(@PathVariable Long id, @RequestParam String name) {
        return ThreadResponse.of(threads.removeTag(id, name));
    }
}
