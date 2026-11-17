package com.example.forum.api;

import com.example.forum.models.*;
import com.example.forum.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.forum.api.ApiDtos.*;

@RestController
@RequestMapping("/api")
//@RequiredArgsConstructor
public class ApiController {
    private final UserService users;
    private final ThreadService threads;
    private final PostService posts;
    private final CommentService comments;
    private final VoteService votes;
    private final AuthService auth;

    public ApiController(UserService users, ThreadService threads, PostService posts, CommentService comments,
            VoteService votes, AuthService auth) {
        this.users = users;
        this.threads = threads;
        this.posts = posts;
        this.comments = comments;
        this.votes = votes;
        this.auth = auth;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = auth.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.of(user));
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.of(auth.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = users.create(new User(null, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user));
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return users.findAll().stream().map(UserResponse::of).toList();
    }

    @GetMapping("/users/{id}")
    public UserResponse user(@PathVariable Long id) {
        return UserResponse.of(users.find(id));
    }

    @PutMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        User input = new User(id, request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole());
        return UserResponse.of(users.update(id, input));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        users.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/threads")
    public ResponseEntity<ThreadResponse> createThread(@Valid @RequestBody ThreadRequest request) {
        ForumThread thread = threads.create(new ForumThread(null,
                new User(request.getAuthorId(), null, null, null, null), request.getTitle()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ThreadResponse.of(thread));
    }

    @GetMapping("/threads")
    public List<ThreadResponse> threadList(@RequestParam(required = false) String title,
                                            @RequestParam(required = false) Long authorId) {
        return threads.findAll(title, authorId).stream().map(ThreadResponse::of).toList();
    }

    @GetMapping("/threads/{id}")
    public ThreadResponse thread(@PathVariable Long id) {
        return ThreadResponse.of(threads.find(id));
    }

    @PutMapping("/threads/{id}")
    public ThreadResponse updateThread(@PathVariable Long id, @Valid @RequestBody ThreadRequest request) {
        ForumThread input = new ForumThread(id,
                new User(request.getAuthorId(), null, null, null, null), request.getTitle());
        return ThreadResponse.of(threads.update(id, input));
    }

    @DeleteMapping("/threads/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable Long id) {
        threads.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/threads/{id}/tags")
    public ThreadResponse addTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        return ThreadResponse.of(threads.addTag(id, request.getName()));
    }

    @DeleteMapping("/threads/{id}/tags")
    public ThreadResponse removeTag(@PathVariable Long id, @RequestParam String name) {
        return ThreadResponse.of(threads.removeTag(id, name));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        Post post = posts.create(new Post(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new ForumThread(request.getThreadId(), null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.of(post));
    }

    @GetMapping("/posts")
    public List<PostResponse> postList(@RequestParam(required = false) Long threadId,
                                       @RequestParam(required = false) Long authorId) {
        return posts.findAll(threadId, authorId).stream().map(PostResponse::of).toList();
    }

    @GetMapping("/posts/{id}")
    public PostResponse post(@PathVariable Long id) {
        return PostResponse.of(posts.find(id));
    }

    @PutMapping("/posts/{id}")
    public PostResponse updatePost(@PathVariable Long id, @Valid @RequestBody ContentUpdateRequest request) {
        return PostResponse.of(posts.update(id, new Post(id, request.getContent(), null, null)));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        posts.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {
        Comment comment = comments.create(new Comment(null, request.getContent(),
                new User(request.getAuthorId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null)));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.of(comment));
    }

    @GetMapping("/comments")
    public List<CommentResponse> commentList(@RequestParam(required = false) Long postId,
                                             @RequestParam(required = false) Long authorId) {
        return comments.findAll(postId, authorId).stream().map(CommentResponse::of).toList();
    }

    @GetMapping("/comments/{id}")
    public CommentResponse comment(@PathVariable Long id) {
        return CommentResponse.of(comments.find(id));
    }

    @PutMapping("/comments/{id}")
    public CommentResponse updateComment(@PathVariable Long id, @Valid @RequestBody ContentUpdateRequest request) {
        return CommentResponse.of(comments.update(id, new Comment(id, request.getContent(), null, null)));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        comments.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/votes")
    public ResponseEntity<VoteResponse> createVote(@Valid @RequestBody VoteRequest request) {
        Vote vote = votes.create(new Vote(null,
                new User(request.getUserId(), null, null, null, null),
                new Post(request.getPostId(), null, null, null), request.getType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(VoteResponse.of(vote));
    }

    @GetMapping("/votes")
    public List<VoteResponse> voteList(@RequestParam(required = false) Long postId) {
        return votes.findAll(postId).stream().map(VoteResponse::of).toList();
    }

    @GetMapping("/votes/{id}")
    public VoteResponse vote(@PathVariable Long id) {
        return VoteResponse.of(votes.find(id));
    }

    @PutMapping("/votes/{id}")
    public VoteResponse updateVote(@PathVariable Long id, @Valid @RequestBody VoteUpdateRequest request) {
        return VoteResponse.of(votes.update(id, new Vote(id, null, null, request.getType())));
    }

    @DeleteMapping("/votes/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        votes.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record UserResponse(Long id, String username, String email, UserRole role) {
        static UserResponse of(User user) {
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        }
    }

    public record ThreadResponse(Long id, String title, Long authorId, List<String> tags) {
        static ThreadResponse of(ForumThread thread) {
            return new ThreadResponse(thread.getId(), thread.getTitle(), thread.getAuthor().getId(),
                    thread.getTags().stream().map(Tag::getName).toList());
        }
    }

    public record PostResponse(Long id, String content, Long authorId, Long threadId) {
        static PostResponse of(Post post) {
            return new PostResponse(post.getId(), post.getContent(), post.getAuthor().getId(),
                    post.getThread().getId());
        }
    }

    public record CommentResponse(Long id, String content, Long authorId, Long postId) {
        static CommentResponse of(Comment comment) {
            return new CommentResponse(comment.getId(), comment.getContent(), comment.getAuthor().getId(),
                    comment.getPost().getId());
        }
    }

    public record VoteResponse(Long id, Long userId, Long postId, VoteType type) {
        static VoteResponse of(Vote vote) {
            return new VoteResponse(vote.getId(), vote.getUser().getId(), vote.getPost().getId(), vote.getType());
        }
    }
}
