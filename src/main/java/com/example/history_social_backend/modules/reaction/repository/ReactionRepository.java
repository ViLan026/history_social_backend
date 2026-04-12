package com.example.history_social_backend.modules.reaction.repository;

import com.example.history_social_backend.modules.reaction.domain.Reaction;
import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionCount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    Optional<Reaction> findByPostIdAndUserId(UUID postId, UUID userId);

    @Query("""
            SELECT new com.example.history_social_backend.modules.reaction.dto.response.ReactionCount(r.type, COUNT(*))
            FROM Reaction r
            WHERE r.postId = :postId
            GROUP BY r.type
            """)
    List<ReactionCount> getReactionStats(@Param("postId") UUID postId);

    Page<Reaction> findByPostId(UUID postId, Pageable pageable);

    Page<Reaction> findByPostIdAndType(UUID postId, ReactionType type, Pageable pageable);
}