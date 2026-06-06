package com.example.history_social_backend.modules.ai.service;

import com.example.history_social_backend.modules.ai.client.AiServiceClient;
import com.example.history_social_backend.modules.ai.dto.request.AiFactCheckRequest;
import com.example.history_social_backend.modules.ai.dto.request.AiHateSpeechRequest;
import com.example.history_social_backend.modules.ai.dto.response.AiFactCheckResponse;
import com.example.history_social_backend.modules.ai.dto.response.AiHateSpeechResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiModerationService {

    private final AiServiceClient aiServiceClient;

    public AiFactCheckResponse factCheckPost(UUID postId, String content) {
        return aiServiceClient.factCheck(
                AiFactCheckRequest.builder()
                        .postId(postId)
                        .content(content)
                        .build()
        );
    }

    public AiHateSpeechResponse detectCommentHateSpeech(String content) {
        return aiServiceClient.detectHateSpeech(
                AiHateSpeechRequest.builder()
                        .text(content)
                        .build()
        );
    }
}