package com.example.history_social_backend.modules.notification.event;
import java.util.UUID;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class CommentCreatedEvent {
    private UUID postId;
    private UUID commentId;
    private UUID actorId;
    private UUID recipientId;
    private String senderName;
}