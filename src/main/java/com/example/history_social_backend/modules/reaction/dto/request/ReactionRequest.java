package com.example.history_social_backend.modules.reaction.dto.request;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionRequest {
    UUID postId;
    ReactionType type;
}