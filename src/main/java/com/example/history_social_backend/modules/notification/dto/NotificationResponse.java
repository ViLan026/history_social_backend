package com.example.history_social_backend.modules.notification.dto;

import com.example.history_social_backend.modules.notification.domain.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    private UUID id;
    private UUID actorId;
    private UUID referenceId;
    private NotificationType type;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}