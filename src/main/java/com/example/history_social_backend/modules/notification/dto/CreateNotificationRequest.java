package com.example.history_social_backend.modules.notification.dto;

import com.example.history_social_backend.modules.notification.domain.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNotificationRequest {
    UUID receiverId;
    UUID actorId;
    NotificationType type;
    String title;
    String message;
    UUID targetId;
    String targetType;
}