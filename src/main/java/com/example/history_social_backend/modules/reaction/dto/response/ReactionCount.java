package com.example.history_social_backend.modules.reaction.dto.response;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;

// lưu số lượng react theo từng loại react 
public record ReactionCount(
        ReactionType type,
        long count
) {
}