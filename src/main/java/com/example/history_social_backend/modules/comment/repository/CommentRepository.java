package com.example.history_social_backend.modules.comment.repository;

import com.example.history_social_backend.modules.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("""
            SELECT c FROM Comment c 
            WHERE c.post.id = :postId 
            AND c.deletedAt IS NULL
            """)
    Page<Comment> findByPostIdAndNotDeleted(@Param("postId") UUID postId, Pageable pageable);
}