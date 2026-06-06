package com.example.history_social_backend.modules.comment.repository;

import com.example.history_social_backend.modules.comment.domain.CommentHateSpeechResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentHateSpeechResultRepository extends JpaRepository<CommentHateSpeechResult, UUID> {

    Optional<CommentHateSpeechResult> findByCommentId(UUID commentId);

    void deleteByCommentId(UUID commentId);
}