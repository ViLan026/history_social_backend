package com.example.history_social_backend.modules.post.service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.notification.event.PostFactCheckCompletedEvent;
import com.example.history_social_backend.modules.post.domain.FactCheckLabel;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostFactCheckClaim;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.post.dto.response.AdminPostDetailResponse;
import com.example.history_social_backend.modules.post.dto.response.AdminPostResponse;
import com.example.history_social_backend.modules.post.dto.response.FactCheckSummaryResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckClaimResponse;
import com.example.history_social_backend.modules.post.repository.PostFactCheckClaimRepository;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;
import com.example.history_social_backend.modules.report.repository.ReportRepository;
import com.example.history_social_backend.modules.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;
    private final PostFactCheckClaimRepository postFactCheckClaimRepository;
    private final ReportRepository reportRepository;
    private final UserQueryService userQueryService;
    private final PostFactCheckService postFactCheckService;
    private final ApplicationEventPublisher eventPublisher;
    

    @Transactional(readOnly = true)
    public Page<AdminPostResponse> getPosts(PostStatus status, Pageable pageable) {
        Page<Post> posts = status == null
                ? postRepository.findAll(pageable)
                : postRepository.findByStatus(status, pageable);

        return posts.map(this::toAdminPostResponse);
    }

    @Transactional(readOnly = true)
    public AdminPostDetailResponse getPostDetail(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        List<PostFactCheckClaimResponse> claims = getFactCheckClaims(post.getId());

        return AdminPostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .authorName(userQueryService.getUserName(post.getAuthorId()))
                .status(post.getStatus())
                .commentCount(post.getCommentCount())
                .reactionCount(post.getReactionCount())
                .bookmarkCount(post.getBookmarkCount())
                .reportCount(getReportCount(post.getId()))
                .factCheckClaims(claims)
                .factCheckSummary(postFactCheckService.buildFactCheckSummary(claims))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }

    @Transactional
    public AdminPostDetailResponse updatePostStatus(UUID postId, PostStatus status) {
        validateAdminStatus(status);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.setStatus(status);
        Post savedPost = postRepository.save(post);

        return getPostDetail(savedPost.getId());
    }

    private AdminPostResponse toAdminPostResponse(Post post) {
        List<PostFactCheckClaimResponse> claims = getFactCheckClaims(post.getId());

        return AdminPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contentPreview(buildPreview(post.getContent()))
                .authorId(post.getAuthorId())
                .authorName(userQueryService.getUserName(post.getAuthorId()))
                .status(post.getStatus())
                .commentCount(post.getCommentCount())
                .reactionCount(post.getReactionCount())
                .bookmarkCount(post.getBookmarkCount())
                .reportCount(getReportCount(post.getId()))
                .factCheckSummary(postFactCheckService.buildFactCheckSummary(claims))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private List<PostFactCheckClaimResponse> getFactCheckClaims(UUID postId) {
        return postFactCheckClaimRepository.findByPostIdOrderByDisplayOrderAsc(postId)
                .stream()
                .map(this::toClaimResponse)
                .toList();
    }

    private PostFactCheckClaimResponse toClaimResponse(PostFactCheckClaim claim) {
        return PostFactCheckClaimResponse.builder()
                .id(claim.getId())
                .claimText(claim.getClaimText())
                .label(claim.getLabel().name())
                .explanation(claim.getExplanation())
                .evidence(claim.getEvidence())
                .displayOrder(claim.getDisplayOrder())
                .build();
    }

    private Long getReportCount(UUID postId) {
        return reportRepository.countByTargetTypeAndTargetId(ReportTargetType.POST, postId);
    }

    private String buildPreview(String content) {
        if (content == null) {
            return "";
        }

        return content.length() > 300
                ? content.substring(0, 300) + "..."
                : content;
    }

    private void validateAdminStatus(PostStatus status) {
        if (status != PostStatus.PUBLISHED
                && status != PostStatus.FLAGGED
                && status != PostStatus.REJECTED) {
            throw new AppException(ErrorCode.INVALID_POST_STATUS);
        }
    }

    @Transactional
    public AdminPostDetailResponse recheckPostFactCheck(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        UUID adminId = SecurityUtils.getCurrentUserId();

        postFactCheckService.recheckPost(post);

        eventPublisher.publishEvent(
                PostFactCheckCompletedEvent.builder()
                        .postId(post.getId())
                        .recipientId(post.getAuthorId())
                        .actorId(adminId)
                        .build());

        return getPostDetail(post.getId());
    }


    
}