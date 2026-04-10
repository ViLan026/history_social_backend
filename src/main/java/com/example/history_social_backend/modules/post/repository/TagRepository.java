package com.example.history_social_backend.modules.post.repository;


import com.example.history_social_backend.modules.post.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByName(String name);

    Set<Tag> findByNameIn(Set<String> names);
}