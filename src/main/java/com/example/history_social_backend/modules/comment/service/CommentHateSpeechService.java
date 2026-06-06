package com.example.history_social_backend.modules.comment.service;

import com.example.history_social_backend.modules.ai.dto.response.AiHateSpeechResponse;
import com.example.history_social_backend.modules.comment.domain.Comment;
import com.example.history_social_backend.modules.comment.domain.CommentHateSpeechResult;
import com.example.history_social_backend.modules.comment.domain.HateSpeechLabel;
import com.example.history_social_backend.modules.comment.repository.CommentHateSpeechResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentHateSpeechService {

    private final CommentHateSpeechResultRepository repository;

    @Transactional
    public void saveResult(Comment comment, AiHateSpeechResponse response) {
        if (comment == null || response == null) {
            return;
        }

        repository.deleteByCommentId(comment.getId());

        HateSpeechLabel label = parseLabel(response);

        CommentHateSpeechResult result = CommentHateSpeechResult.builder()
                .comment(comment)
                .label(label)
                .score(response.getScore())
                .rawResult(Map.of(
                        "label", response.getLabel(),
                        "score", response.getScore(),
                        "hateSpeech", response.getHateSpeech()
                ))
                .build();

        repository.save(result);
    }

    public boolean isHateSpeech(AiHateSpeechResponse response) {
        if (response == null) {
            return false;
        }

        if (Boolean.TRUE.equals(response.getHateSpeech())) {
            return true;
        }

        return "HATE".equalsIgnoreCase(response.getLabel())
                || "LABEL_1".equalsIgnoreCase(response.getLabel());
    }

    private HateSpeechLabel parseLabel(AiHateSpeechResponse response) {
        return isHateSpeech(response) ? HateSpeechLabel.HATE : HateSpeechLabel.CLEAN;
    }
}