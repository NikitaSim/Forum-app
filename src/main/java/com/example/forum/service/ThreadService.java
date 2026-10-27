package com.example.forum.service;

import com.example.forum.models.ForumThread;
import com.example.forum.models.Tag;
import com.example.forum.repository.TagRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Transactional
public class ThreadService {
    private final ThreadRepository repository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public ThreadService(ThreadRepository repository, TagRepository tagRepository, UserRepository userRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public ForumThread create(ForumThread thread) {
        validate(thread);
        thread.setAuthor(userRepository.findById(thread.getAuthor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found")));
        return repository.save(thread);
    }
    @Transactional(readOnly=true)
    public List<ForumThread> findAll(String title, Long authorId) {
        if (title != null && !title.isBlank()) return repository.findAllByTitleContainingIgnoreCase(title.trim());
        if (authorId != null) return repository.findAllByAuthorId(authorId);
        return repository.findAll();
    }
    @Transactional(readOnly=true)
    public ForumThread find(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Thread not found")); }
    public ForumThread update(Long id, ForumThread input) {
        ForumThread t=find(id);
        if (input.getTitle()==null || input.getTitle().isBlank()) throw new IllegalArgumentException("Title is required");
        t.setTitle(input.getTitle());
        if (input.getAuthor()!=null && input.getAuthor().getId()!=null)
            t.setAuthor(userRepository.findById(input.getAuthor().getId()).orElseThrow(() -> new IllegalArgumentException("Author not found")));
        return repository.save(t);
    }
    public void delete(Long id) { repository.delete(find(id)); }

    public ForumThread addTag(Long id, String name) {
        ForumThread t=find(id);
        if(name==null || name.isBlank()) throw new IllegalArgumentException("Tag is required");
        Tag tag=tagRepository.findByNameIgnoreCase(name.trim()).orElseGet(() -> tagRepository.save(new Tag(null,name.trim())));
        t.addTag(tag); return repository.save(t);
    }
    public ForumThread removeTag(Long id, String name) {
        ForumThread t=find(id);
        t.getTags().removeIf(tag -> tag.getName().equalsIgnoreCase(name));
        return repository.save(t);
    }
    @Transactional(readOnly=true)
    public List<String> tags(Long id) { return find(id).getTags().stream().map(Tag::getName).toList(); }

    private void validate(ForumThread t) {
        if(t==null || t.getTitle()==null || t.getTitle().isBlank()) throw new IllegalArgumentException("Title is required");
        if(t.getAuthor()==null || t.getAuthor().getId()==null) throw new IllegalArgumentException("Author is required");
    }
}
