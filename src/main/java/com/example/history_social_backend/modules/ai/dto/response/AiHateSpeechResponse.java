package com.example.history_social_backend.modules.ai.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHateSpeechResponse {
    private String label;
    private Double score;
    private Boolean hateSpeech;
}