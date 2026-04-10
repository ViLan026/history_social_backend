// post/repository/PostRepository.java
package com.example.history_social_backend.modules.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    /** Fetch Post kèm media và sources trong một query để tránh N+1. */
    @Query("""
            SELECT DISTINCT p FROM Post p
            LEFT JOIN FETCH p.mediaList
            LEFT JOIN FETCH p.sources
            WHERE p.id = :id
            """)
    Optional<Post> findByIdWithDetails(@Param("id") UUID id);

    Page<Post> findByAuthorId(UUID authorId, Pageable pageable);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    /** Tìm kiếm full-text đơn giản trong title và content. */
    @Query("""
            SELECT p FROM Post p
            WHERE LOWER(p.title)   LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** Tăng view count bằng UPDATE trực tiếp, tránh load entity. */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id);
}