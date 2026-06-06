package com.example.history_social_backend.modules.ai.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHateSpeechRequest {
    private String text;
}