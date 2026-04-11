package com.example.history_social_backend.modules.reaction.dto.response;

import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionDetailResponse {
    UUID userId;
    String displayName;
    String avatarUrl;
    ReactionType type;
}