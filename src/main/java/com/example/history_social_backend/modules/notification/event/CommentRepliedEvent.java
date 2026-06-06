package com.example.history_social_backend.modules.notification.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepliedEvent {

    UUID postId;
    UUID parentCommentId;
    UUID replyCommentId;
    UUID actorId;
    UUID recipientId;
    String senderName;
}