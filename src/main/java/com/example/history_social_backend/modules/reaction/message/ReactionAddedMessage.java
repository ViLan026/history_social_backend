package com.example.history_social_backend.modules.reaction.message;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionAddedMessage {
    private UUID postId;
    private UUID userId;
    private ReactionType reactionType;
}