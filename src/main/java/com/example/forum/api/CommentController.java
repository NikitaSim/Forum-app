package com.example.forum.api;

import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService comments;

    public CommentController(CommentService comments) {
        this.comments = comments;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentRequest request) {
        Comment comment = comments.create(new Comment(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @GetMapping
    public List<CommentResponse> findAll(@RequestParam(required = false) Long postId,
                                         @RequestParam(required = false) Long authorId) {
        return comments.findAll(postId, authorId).stream().map(CommentResponse::of).toList();
    }

    @GetMapping("/{id}")
    public CommentResponse find(@PathVariable Long id) {
        return CommentResponse.of(comments.find(id));
    }

    @PutMapping("/{id}")
    public CommentResponse update(@PathVariable Long id, @Valid @RequestBody ContentUpdateRequest request) {
        return CommentResponse.of(comments.update(id, new Comment(id, request.getContent(), null, null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        comments.delete(id);
        return ResponseEntity.noContent().build();
    }
}
