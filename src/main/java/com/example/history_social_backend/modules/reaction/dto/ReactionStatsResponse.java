package com.example.history_social_backend.modules.reaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionStatsResponse {
    private long totalReactions;
    private List<ReactionCount> counts;
}