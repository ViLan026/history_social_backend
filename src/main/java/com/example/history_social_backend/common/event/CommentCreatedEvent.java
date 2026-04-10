package com.example.history_social_backend.common.event;

import java.util.UUID;

public record CommentCreatedEvent(
        UUID commentId,
        UUID postId,
        UUID authorId
) {
}   