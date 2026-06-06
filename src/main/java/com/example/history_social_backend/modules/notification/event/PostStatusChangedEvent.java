package com.example.history_social_backend.modules.notification.event;
import java.util.UUID;

import com.example.history_social_backend.modules.post.domain.PostStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostStatusChangedEvent {
    UUID postId;
    UUID recipientId;
    PostStatus status;
    String reason;
    UUID actorId;
}