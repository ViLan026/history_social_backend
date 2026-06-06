package com.example.history_social_backend.modules.ai.client;

import com.example.history_social_backend.modules.ai.dto.request.AiFactCheckRequest;
import com.example.history_social_backend.modules.ai.dto.request.AiHateSpeechRequest;
import com.example.history_social_backend.modules.ai.dto.response.AiFactCheckResponse;
import com.example.history_social_backend.modules.ai.dto.response.AiHateSpeechResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AiServiceClient {

    private final RestClient aiRestClient;

    public AiFactCheckResponse factCheck(AiFactCheckRequest request) {
        return aiRestClient.post()
                .uri("/fact-check")
                .body(request)
                .retrieve()
                .body(AiFactCheckResponse.class);
    }

    public AiHateSpeechResponse detectHateSpeech(AiHateSpeechRequest request) {
        return aiRestClient.post()
                .uri("/hate-speech/detect")
                .body(request)
                .retrieve()
                .body(AiHateSpeechResponse.class);
    }
}