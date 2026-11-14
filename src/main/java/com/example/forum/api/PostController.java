package com.example.forum.api;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService posts;

    public PostController(PostService posts) {
        this.posts = posts;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        Post post = posts.create(new Post(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new ForumThread(request.getThreadId(), null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.of(post));
    }

    @GetMapping
    public List<PostResponse> findAll(@RequestParam(required = false) Long threadId,
                                      @RequestParam(required = false) Long authorId) {
        return posts.findAll(threadId, authorId).stream().map(PostResponse::of).toList();
    }

    @GetMapping("/{id}")
    public PostResponse find(@PathVariable Long id) {
        return PostResponse.of(posts.find(id));
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody ContentUpdateRequest request) {
        return PostResponse.of(posts.update(id, new Post(id, request.getContent(), null, null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        posts.delete(id);
        return ResponseEntity.noContent().build();
    }
}
