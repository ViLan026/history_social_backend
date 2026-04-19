package com.example.history_social_backend.modules.post.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "moderation_queue")
public class ModerationQueue extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;

    @Column(name = "user_id", nullable = false)
    UUID userId;

    @Column(columnDefinition = "TEXT")
    String reason;

    @Enumerated(EnumType.STRING)
    @Default
    ModerationStatus status = ModerationStatus.PENDING;

    Long adminId;

    LocalDateTime reviewedAt;

}