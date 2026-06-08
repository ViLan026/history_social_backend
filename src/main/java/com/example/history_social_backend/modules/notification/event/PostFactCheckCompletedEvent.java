package com.example.history_social_backend.modules.notification.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class PostFactCheckCompletedEvent {

    // UUID commentId;
    UUID postId;
    UUID actorId;
    UUID recipientId;
    String reason;
}
