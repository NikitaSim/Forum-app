
package com.example.forum.service;

import com.example.forum.exception.InvalidRequestException;
import com.example.forum.exception.UserNotFoundException;
import com.example.forum.models.ForumThread;
import com.example.forum.models.Tag;
import com.example.forum.models.User;
import com.example.forum.models.UserRole;
import com.example.forum.repository.TagRepository;
import com.example.forum.repository.ThreadRepository;
import com.example.forum.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    @Mock
    ThreadRepository repository;

    @Mock
    UserRepository userRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    ThreadService service;

    @Test
    void createLoadsAuthorAndSavesThread() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(repository.save(any(ForumThread.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ForumThread result = service.create(
                new ForumThread(null, new User(1L, null, null, null, null), "Go"),
                List.of()
        );

        assertEquals("Go", result.getTitle());
        assertSame(author, result.getAuthor());

        verify(repository).save(result);
        verify(userRepository).findById(1L);
        verifyNoInteractions(tagRepository);
    }

    @Test
    void createSavesThreadWithTags() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", UserRole.USER);
        Tag javaTag = new Tag(1L, "java");
        Tag springTag = new Tag(2L, "spring");

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(javaTag));
        when(tagRepository.findByNameIgnoreCase("spring"))
                .thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class)))
                .thenReturn(springTag);
        when(repository.save(any(ForumThread.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ForumThread result = service.create(
                new ForumThread(null, new User(1L, null, null, null, null), "Spring Boot"),
                List.of("java", "spring")
        );

        assertEquals("Spring Boot", result.getTitle());
        assertSame(author, result.getAuthor());
        assertEquals(2, result.getTags().size());
        assertTrue(result.getTags().contains(javaTag));
        assertTrue(result.getTags().contains(springTag));

        verify(tagRepository).findByNameIgnoreCase("java");
        verify(tagRepository).findByNameIgnoreCase("spring");
        verify(tagRepository).save(any(Tag.class));
        verify(repository).save(result);
    }

    @Test
    void createRejectsBlankTitle() {
        assertThrows(InvalidRequestException.class,
                () -> service.create(
                        new ForumThread(null, new User(1L, null, null, null, null), " "),
                        List.of()
                ));

        verifyNoInteractions(repository, userRepository, tagRepository);
    }

    @Test
    void createRejectsBlankTag() {
        User author = new User(1L, "nikita", "n@mail.ru", "hash", UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(InvalidRequestException.class,
                () -> service.create(
                        new ForumThread(null, new User(1L, null, null, null, null), "Go"),
                        List.of(" ")
                ));

        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.create(
                        new ForumThread(null, new User(1L, null, null, null, null), "Go"),
                        List.of()
                ));

        verify(repository, never()).save(any());
    }
}
