package com.example.history_social_backend.modules.reaction.service;

import com.example.history_social_backend.common.event.ReactionAddedEvent;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.reaction.domain.Reaction;
import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import com.example.history_social_backend.modules.reaction.dto.request.ReactionRequest;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionCount;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionDetailResponse;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionStatsResponse;
import com.example.history_social_backend.modules.reaction.repository.ReactionRepository;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import com.example.history_social_backend.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Transactional
    public ReactionType toggleReaction(ReactionRequest request) {
        UUID postId = request.getPostId();
        UUID authorId = SecurityUtils.getCurrentUserId();

        ReactionType newType = request.getType();

        // Tìm reaction hiện tại của user trên post
        Reaction existingReaction = reactionRepository
                .findByPostIdAndUserId(postId, authorId)
                .orElse(null);

        if (existingReaction == null) {

            Reaction newReaction = new Reaction();
            newReaction.setPostId(postId);
            newReaction.setUserId(authorId);
            newReaction.setType(newType);

            reactionRepository.save(newReaction);

            // Publish event CHỈ khi tạo reaction mới
            eventPublisher.publishEvent(new ReactionAddedEvent(
                    postId,
                    authorId,
                    newType));

            return newReaction.getType();

        } else if (existingReaction.getType() != newType) {
            // đổi react
            existingReaction.setType(newType);
            reactionRepository.save(existingReaction);

            return existingReaction.getType();

        } else {
            // xóa react
            reactionRepository.delete(existingReaction);
            return null;
        }

    }

    @Transactional(readOnly = true)
    public ReactionStatsResponse getReactionStats(UUID postId) {
        List<ReactionCount> counts = reactionRepository.getReactionStats(postId);

        long totalReactions = counts.stream()
                .mapToLong(ReactionCount::count)
                .sum();

        Map<ReactionType, Long> map = counts.stream()
                .collect(Collectors.toMap(ReactionCount::type, ReactionCount::count));

        List<ReactionCount> fullCounts = Arrays.stream(ReactionType.values())
                .map(type -> new ReactionCount(type, map.getOrDefault(type, 0L)))
                .toList();

        return ReactionStatsResponse.builder()
                .totalReactions(totalReactions)
                .counts(fullCounts)
                .build();
    }

    public PageResponse<ReactionDetailResponse> getReactionDetails(UUID postId, ReactionType type, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Reaction> reactionPage;

        // Fetch data
        if (type != null) {
            reactionPage = reactionRepository.findByPostIdAndType(postId, type, pageable);
        } else {
            reactionPage = reactionRepository.findByPostId(postId, pageable);
        }

        if (reactionPage.isEmpty()) {
            return PageResponse.from(Page.empty(pageable));
        }

        // Lấy thông tin user (Bulk fetch)
        Set<UUID> userIds = reactionPage.getContent().stream()
                .map(Reaction::getUserId)
                .collect(Collectors.toSet());

        Map<UUID, UserReactionResponse> userInfoMap = userService.getUserReactionInfoMap(userIds);

        // Map sang DTO
        Page<ReactionDetailResponse> detailsPage = reactionPage.map(reaction -> {
            UserReactionResponse userInfo = userInfoMap.get(reaction.getUserId());
            return ReactionDetailResponse.builder()
                    .userId(reaction.getUserId())
                    .displayName(userInfo != null ? userInfo.getDisplayName() : "Người dùng ẩn danh")
                    .avatarUrl(userInfo != null ? userInfo.getAvatarUrl() : null)
                    .type(reaction.getType())
                    .build();
        });

        // Đóng gói thành PageResponse chuẩn ngay tại Service
        return PageResponse.from(detailsPage);
    }


}