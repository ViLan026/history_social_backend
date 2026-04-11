package com.example.history_social_backend.modules.reaction.repository;

import com.example.history_social_backend.modules.reaction.domain.Reaction;
import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionCount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    Optional<Reaction> findByPostIdAndUserId(UUID postId, UUID userId);

    @Query("""
            SELECT new ReactionCount(r.type, COUNT(r))
            FROM Reaction r
            WHERE r.post.id = :postId
            GROUP BY r.type
            """)
    List<ReactionCount> getReactionStats(@Param("postId") UUID postId);

    Page<Reaction> findByPost(UUID postId, Pageable pageable);

    Page<Reaction> findByPostAndType(UUID postId, ReactionType type, Pageable pageable);
}