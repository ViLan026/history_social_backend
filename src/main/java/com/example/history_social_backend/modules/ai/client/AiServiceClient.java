package com.example.history_social_backend.modules.ai.client;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.ai.dto.request.AiFactCheckRequest;
import com.example.history_social_backend.modules.ai.dto.request.AiHateSpeechRequest;
import com.example.history_social_backend.modules.ai.dto.response.AiFactCheckResponse;
import com.example.history_social_backend.modules.ai.dto.response.AiHateSpeechResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiServiceClient {

    private final WebClient aiWebClient;

    public AiFactCheckResponse factCheck(AiFactCheckRequest request) {
        return aiWebClient.post()
                .uri("/fact-check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "post_id", request.getPostId(),
                        "content", request.getContent()
                ))
                .retrieve()
                .bodyToMono(AiFactCheckResponse.class)
                .block();
    }

    public AiHateSpeechResponse detectHateSpeech(AiHateSpeechRequest request) {
        if (request == null || request.getText() == null || request.getText().isBlank()) {
            throw new AppException(ErrorCode.INVALID_COMMENT_CONTENT);
        }

        System.out.printf("Calling HSD FastAPI with text=%s%n", request.getText());

        return aiWebClient.post()
                .uri("/hate-speech/detect")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", request.getText()))
                .retrieve()
                .bodyToMono(AiHateSpeechResponse.class)
                .block();
    }
}