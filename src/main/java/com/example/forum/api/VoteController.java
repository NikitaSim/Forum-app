package com.example.forum.api;

import com.example.forum.models.Post;
import com.example.forum.models.User;
import com.example.forum.models.Vote;
import com.example.forum.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService votes;

    public VoteController(VoteService votes) {
        this.votes = votes;
    }

    @PostMapping
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody VoteRequest request) {
        Vote vote = votes.create(new Vote(null,
                new User(request.getUserId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null), request.getType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(VoteResponse.of(vote));
    }

    @GetMapping
    public List<VoteResponse> findAll(@RequestParam(required = false) Long postId) {
        return votes.findAll(postId).stream().map(VoteResponse::of).toList();
    }

    @GetMapping("/{id}")
    public VoteResponse find(@PathVariable Long id) {
        return VoteResponse.of(votes.find(id));
    }

    @PutMapping("/{id}")
    public VoteResponse update(@PathVariable Long id, @Valid @RequestBody VoteUpdateRequest request) {
        return VoteResponse.of(votes.update(id, new Vote(id, null, null, request.getType())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        votes.delete(id);
        return ResponseEntity.noContent().build();
    }
}
