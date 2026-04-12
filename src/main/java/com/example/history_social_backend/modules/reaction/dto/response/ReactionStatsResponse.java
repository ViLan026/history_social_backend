package com.example.history_social_backend.modules.reaction.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionStatsResponse {
    long totalReactions;
    List<ReactionCount> counts;
}