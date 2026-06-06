package com.example.history_social_backend.modules.notification.event;
import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreatedEvent {
    UUID postId;
    UUID commentId;
    UUID actorId;
    UUID recipientId;
    String senderName;
}