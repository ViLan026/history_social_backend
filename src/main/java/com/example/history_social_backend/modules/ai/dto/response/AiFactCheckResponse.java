package com.example.history_social_backend.modules.ai.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFactCheckResponse {
    private UUID postId;
    // private Double qualityScore;
    private String postLabel;
    private List<AiClaimResponse> claims;
}