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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Transactional
    public void toggleReaction(ReactionRequest request) {
        UUID postId = request.getPostId();
        UUID authorId = SecurityUtils.getCurrentUserId();

        ReactionType newType = request.getType();

        // Tìm reaction hiện tại của user trên post
        Reaction existingReaction = reactionRepository
                .findByPostIdAndUserId(postId, authorId)
                .orElse(null);

        if (existingReaction == null) {

            Reaction newReaction = new Reaction();
            newReaction.setPost(postId);
            newReaction.setUser(authorId);
            newReaction.setType(newType);

            reactionRepository.save(newReaction);

            // Publish event CHỈ khi tạo reaction mới
            eventPublisher.publishEvent(new ReactionAddedEvent(
                    postId,
                    authorId,
                    newType));

        } else if (existingReaction.getType() != newType) {
            // đổi react
            existingReaction.setType(newType);
            reactionRepository.save(existingReaction);
            // KHÔNG publish event khi chỉ đổi type

        } else {
            // xóa react
            reactionRepository.delete(existingReaction);
            // KHÔNG publish event khi xóa
        }
    }

    @Transactional(readOnly = true)
    public ReactionStatsResponse getReactionStats(UUID postId) {
        List<ReactionCount> counts = reactionRepository.getReactionStats(postId);

        long totalReactions = counts.stream()
                .mapToLong(ReactionCount::count)
                .sum();

        return ReactionStatsResponse.builder()
                .totalReactions(totalReactions)
                .counts(counts)
                .build();
    }

    public PageResponse<ReactionDetailResponse> getReactionDetails(UUID postId, ReactionType type, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Reaction> reactionPage;

        //  Fetch data
        if (type != null) {
            reactionPage = reactionRepository.findByPostAndType(postId, type, pageable);
        } else {
            reactionPage = reactionRepository.findByPost(postId, pageable);
        }

        if (reactionPage.isEmpty()) {
            return PageResponse.from(Page.empty(pageable));
        }

        //  Lấy thông tin user (Bulk fetch)
        Set<UUID> userIds = reactionPage.getContent().stream()
                .map(Reaction::getUser)
                .collect(Collectors.toSet());

        Map<UUID, UserReactionResponse> userInfoMap = userService.getUserReactionInfoMap(userIds);

        // Map sang DTO
        Page<ReactionDetailResponse> detailsPage = reactionPage.map(reaction -> {
            UserReactionResponse userInfo = userInfoMap.get(reaction.getUser());
            return ReactionDetailResponse.builder()
                    .userId(reaction.getUser())
                    .displayName(userInfo != null ? userInfo.getDisplayName() : "Người dùng ẩn danh")
                    .avatarUrl(userInfo != null ? userInfo.getAvatarUrl() : null)
                    .type(reaction.getType())
                    .build();
        });

        //  Đóng gói thành PageResponse chuẩn ngay tại Service
        return PageResponse.from(detailsPage);
    }

    // public Page<ReactionDetailResponse> getReactionDetails(UUID postId, ReactionType type, Pageable pageable) {
    //     Page<Reaction> reactionPage;

    //     // Nếu có truyền type thì lọc theo type, không thì lấy tất cả
    //     if (type != null) {
    //         reactionPage = reactionRepository.findByPostAndType(postId, type, pageable);
    //     } else {
    //         reactionPage = reactionRepository.findByPost(postId, pageable);
    //     }

    //     if (reactionPage.isEmpty()) {
    //         return Page.empty();
    //     }

    //     // Lấy danh sách user IDs từ các reaction trong trang hiện tại
    //     Set<UUID> userIds = reactionPage.getContent().stream()
    //             .map(Reaction::getUser)
    //             .collect(Collectors.toSet());

    //     // Lấy thông tin user từ Module User thông qua Service
    //     Map<UUID, UserReactionResponse> userInfoMap = userService.getUserReactionInfoMap(userIds);

    //     // Map dữ liệu Reaction và User Info lại với nhau
    //     return reactionPage.map(reaction -> {
    //         UserReactionResponse userInfo = userInfoMap.get(reaction.getUser());

    //         return ReactionDetailResponse.builder()
    //                 .userId(reaction.getUser())
    //                 .displayName(userInfo != null ? userInfo.getDisplayName() : "Người dùng ẩn danh")
    //                 .avatarUrl(userInfo != null ? userInfo.getAvatarUrl() : null)
    //                 .type(reaction.getType())
    //                 .build();
    //     });
    // }
}