package com.example.forum.service;

import com.example.forum.models.ForumThread;
import com.example.forum.exception.UserNotFoundException;
import com.example.forum.exception.ThreadNotFoundException;
import com.example.forum.exception.InvalidRequestException;
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

    // public ForumThread create(ForumThread thread) {
    //     validate(thread);
    //     thread.setAuthor(userRepository.findById(thread.getAuthor().getId())
    //             .orElseThrow(() -> new UserNotFoundException(thread.getAuthor().getId())));
    //     return repository.save(thread);
    // }

    public ForumThread create(ForumThread thread, List<String> tagNames) {
        validate(thread);

        thread.setAuthor(userRepository.findById(thread.getAuthor().getId())
                .orElseThrow(() -> new UserNotFoundException(thread.getAuthor().getId())));

        if (tagNames != null) {
            for (String name : tagNames) {
                if (name == null || name.isBlank()) {
                    throw new InvalidRequestException("Название тега не может быть пустым.");
                }

                String normalizedName = name.trim();

                Tag tag = tagRepository.findByNameIgnoreCase(normalizedName)
                        .orElseGet(() -> tagRepository.save(new Tag(null, normalizedName)));

                thread.addTag(tag);
            }
        }

        return repository.save(thread);
    }
    
    @Transactional(readOnly=true)
    public List<ForumThread> findAll(String title, Long authorId) {
        if (title != null && !title.isBlank()) return repository.findAllByTitleContainingIgnoreCase(title.trim());
        if (authorId != null) return repository.findAllByAuthorId(authorId);
        return repository.findAll();
    }
    @Transactional(readOnly=true)
    public ForumThread find(Long id) { return repository.findById(id).orElseThrow(() -> new ThreadNotFoundException(id)); }
    public ForumThread update(Long id, ForumThread input) {
        ForumThread t=find(id);
        if (input.getTitle()==null || input.getTitle().isBlank()) throw new InvalidRequestException("Название треда обязательно.");
        t.setTitle(input.getTitle());
        if (input.getAuthor()!=null && input.getAuthor().getId()!=null)
            t.setAuthor(userRepository.findById(input.getAuthor().getId()).orElseThrow(() -> new UserNotFoundException(input.getAuthor().getId())));
        return repository.save(t);
    }
    public void delete(Long id) { repository.delete(find(id)); }

    public ForumThread addTag(Long id, String name) {
        ForumThread t=find(id);
        if(name==null || name.isBlank()) throw new InvalidRequestException("Название тега обязательно.");
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
        if(t==null || t.getTitle()==null || t.getTitle().isBlank()) throw new InvalidRequestException("Название треда обязательно.");
        if(t.getAuthor()==null || t.getAuthor().getId()==null) throw new InvalidRequestException("Необходимо указать автора треда.");
    }
}
