package com.example.history_social_backend.modules.post.repository;

import com.example.history_social_backend.modules.post.domain.PostFactCheckClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostFactCheckClaimRepository extends JpaRepository<PostFactCheckClaim, UUID> {

    List<PostFactCheckClaim> findByPostIdOrderByDisplayOrderAsc(UUID postId);

    void deleteByPostId(UUID postId);

    boolean existsByPostId(UUID postId);

    @Query("""
            SELECT DISTINCT c.post.id
            FROM PostFactCheckClaim c
            WHERE c.post.id IN :postIds
            """)
    Set<UUID> findPostIdsHavingFactCheck(@Param("postIds") Collection<UUID> postIds);

    long countByPostId(UUID postId);
}