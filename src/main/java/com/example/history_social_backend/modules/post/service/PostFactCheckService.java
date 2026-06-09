package com.example.history_social_backend.modules.post.service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.ai.dto.response.AiClaimResponse;
import com.example.history_social_backend.modules.ai.dto.response.AiFactCheckResponse;
import com.example.history_social_backend.modules.ai.service.AiModerationService;
import com.example.history_social_backend.modules.post.domain.FactCheckLabel;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostFactCheckClaim;
import com.example.history_social_backend.modules.post.dto.response.FactCheckSummaryResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckClaimPreviewResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckClaimResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckDetailResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckPreviewResponse;
import com.example.history_social_backend.modules.post.repository.PostFactCheckClaimRepository;
import com.example.history_social_backend.modules.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostFactCheckService {

    private final PostFactCheckClaimRepository postFactCheckClaimRepository;
    private final AiModerationService aiModerationService;
    private final PostRepository postRepository;

    @Transactional
    public void recheckPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        recheckPost(post);
    }

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

    public FactCheckSummaryResponse buildFactCheckSummary(List<PostFactCheckClaimResponse> claims) {
        long supported = claims.stream()
                .filter(c -> FactCheckLabel.SUPPORTED.name().equals(c.getLabel()))
                .count();

        long refuted = claims.stream()
                .filter(c -> FactCheckLabel.REFUTED.name().equals(c.getLabel()))
                .count();

        long notEnough = claims.stream()
                .filter(c -> FactCheckLabel.NOT_ENOUGH_EVIDENCE.name().equals(c.getLabel()))
                .count();

        return FactCheckSummaryResponse.builder()
                .supportedCount(supported)
                .refutedCount(refuted)
                .notEnoughEvidenceCount(notEnough)
                .build();
    }

    @Transactional(readOnly = true)
    public PostFactCheckPreviewResponse getFactCheckPreview(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        List<PostFactCheckClaimPreviewResponse> claims = postFactCheckClaimRepository
                .findByPostIdOrderByDisplayOrderAsc(postId)
                .stream()
                .map(claim -> PostFactCheckClaimPreviewResponse.builder()
                        .id(claim.getId())
                        .claimText(claim.getClaimText())
                        .label(claim.getLabel().name())
                        .explanation(claim.getExplanation())
                        .displayOrder(claim.getDisplayOrder())
                        .build())
                .toList();

        return PostFactCheckPreviewResponse.builder()
                .postId(postId)
                .claims(claims)
                .build();
    }

    @Transactional(readOnly = true)
    public PostFactCheckDetailResponse getFactCheckDetail(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        List<PostFactCheckClaimResponse> claims = postFactCheckClaimRepository
                .findByPostIdOrderByDisplayOrderAsc(postId)
                .stream()
                .map(claim -> PostFactCheckClaimResponse.builder()
                        .id(claim.getId())
                        .claimText(claim.getClaimText())
                        .label(claim.getLabel().name())
                        .explanation(claim.getExplanation())
                        .evidence(claim.getEvidence())
                        .displayOrder(claim.getDisplayOrder())
                        .build())
                .toList();

        return PostFactCheckDetailResponse.builder()
                .postId(postId)
                .summary(buildFactCheckSummary(claims))
                .claims(claims)
                .build();
    }

}