package com.example.history_social_backend.modules.notification.event;
import java.util.UUID;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class PostStatusChangedEvent {
    private UUID postId;
    private UUID recipientId;
    private String status;
    private String reason;
}