package com.example.history_social_backend.modules.notification.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.modules.notification.domain.NotificationType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationUserResponse {
    UUID id;
    UUID actorId;
    UUID referenceId;
    UUID recipientId;

    NotificationType type;
    String content;
    boolean read;
    LocalDateTime createdAt;

    String displayName;
    String avatarUrl;

    UUID postId;
    UUID commentId;
    UUID reportId;
}
