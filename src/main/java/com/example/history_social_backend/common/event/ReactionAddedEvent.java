package com.example.history_social_backend.common.event;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import java.util.UUID;

public record ReactionAddedEvent(
        UUID postId,
        UUID userId,
        ReactionType reactionType
) {
}