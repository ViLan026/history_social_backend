package com.example.history_social_backend.modules.reaction.dto;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;

/**
 * Projection cho thống kê reaction
 */
public record ReactionCount(
        ReactionType type,
        long count
) {
}