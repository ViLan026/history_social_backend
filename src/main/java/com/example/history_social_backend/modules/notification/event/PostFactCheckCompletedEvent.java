package com.example.history_social_backend.modules.notification.event;

import java.util.UUID;
import lombok.*;

@Getter
@Builder
public class PostFactCheckCompletedEvent {
    private UUID postId;
    private UUID recipientId;
}
