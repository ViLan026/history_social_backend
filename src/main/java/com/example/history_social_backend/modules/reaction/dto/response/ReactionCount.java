package com.example.history_social_backend.modules.reaction.dto.response;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;

public record ReactionCount(
        ReactionType type,
        long count
) {
}