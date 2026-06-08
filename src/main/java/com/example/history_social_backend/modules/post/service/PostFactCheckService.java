package com.example.history_social_backend.modules.post.service;

import com.example.history_social_backend.modules.ai.dto.response.AiClaimResponse;
import com.example.history_social_backend.modules.ai.dto.response.AiFactCheckResponse;
import com.example.history_social_backend.modules.ai.service.AiModerationService;
import com.example.history_social_backend.modules.post.domain.FactCheckLabel;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostFactCheckClaim;
import com.example.history_social_backend.modules.post.repository.PostFactCheckClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFactCheckService {

    private final PostFactCheckClaimRepository postFactCheckClaimRepository;
    private final AiModerationService aiModerationService;

    @Transactional
    public void recheckPost(Post post) {
        AiFactCheckResponse response = aiModerationService.factCheckPost(
                post.getId(),
                post.getContent());

        saveFactCheckClaims(post, response);
    }

    @Transactional
    public void saveFactCheckClaims(Post post, AiFactCheckResponse response) {
        postFactCheckClaimRepository.deleteByPostId(post.getId());

        if (response == null || response.getClaims() == null || response.getClaims().isEmpty()) {
            return;
        }

        List<PostFactCheckClaim> claims = response.getClaims()
                .stream()
                .map(claim -> toEntity(post, claim, response.getClaims().indexOf(claim)))
                .toList();

        postFactCheckClaimRepository.saveAll(claims);
    }

    private PostFactCheckClaim toEntity(Post post, AiClaimResponse claim, int index) {
        return PostFactCheckClaim.builder()
                .post(post)
                .claimText(claim.getClaim())
                .label(parseLabel(claim.getLabel()))
                .explanation(claim.getExplanation())
                .evidence(claim.getEvidence())
                .displayOrder(index + 1)
                .build();
    }

    private FactCheckLabel parseLabel(String label) {
        try {
            return FactCheckLabel.valueOf(label);
        } catch (Exception e) {
            return FactCheckLabel.NOT_ENOUGH_EVIDENCE;
        }
    }

}