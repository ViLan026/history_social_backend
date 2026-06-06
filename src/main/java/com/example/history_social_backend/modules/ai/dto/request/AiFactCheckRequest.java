package com.example.history_social_backend.modules.ai.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFactCheckRequest {
    private UUID postId;
    private String content;
}