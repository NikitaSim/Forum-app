package com.example.forum.repository;

import com.example.forum.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {










    Optional<Tag> findByNameIgnoreCase(String name);
}
