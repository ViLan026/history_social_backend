package com.example.history_social_backend.modules.comment.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "post_id", nullable = false)
    UUID postId;

    @Column(name = "author_id", nullable = false)
    UUID authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    public void validateContent() {
        if (content == null || content.replaceAll("[\\p{P}\\s]", "").isEmpty()) {
            throw new AppException(ErrorCode.INVALID_COMMENT_CONTENT);
        }
    }

    public void markAsDeleted() {
        if (this.deletedAt != null) {
            throw new AppException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
        this.deletedAt = LocalDateTime.now();
    }
}