package com.example.history_social_backend.modules.reaction.service;

import com.example.history_social_backend.common.event.ReactionAddedEvent;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.reaction.domain.Reaction;
import com.example.history_social_backend.modules.reaction.dto.ReactionCount;
import com.example.history_social_backend.modules.reaction.dto.ReactionRequest;
import com.example.history_social_backend.modules.reaction.dto.ReactionStatsResponse;
import com.example.history_social_backend.modules.reaction.repository.ReactionRepository;
import com.example.history_social_backend.modules.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Toggle Reaction - Xử lý 3 case trong 1 method:
     * 1. Chưa có reaction → Tạo mới + Publish Event
     * 2. Đã có nhưng khác type → Cập nhật type
     * 3. Đã có và cùng type → Xóa (Unlike)
     */
    @Transactional
    public void toggleReaction(ReactionRequest request) {
        UUID postId = request.getPostId();
        UUID userId = UUID.fromString(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication().getName()
        );

        // Tìm reaction hiện tại của user trên post
        Reaction existingReaction = reactionRepository
                .findByPostIdAndUserId(postId, userId)
                .orElse(null);

        if (existingReaction == null) {
            // ==================== CASE 1: TẠO MỚI ====================
            Post postRef = entityManager.getReference(Post.class, postId);
            User userRef = entityManager.getReference(User.class, userId);

            Reaction newReaction = new Reaction();
            newReaction.setPost(postRef);
            newReaction.setUser(userRef);
            newReaction.setType(request.getType());

            Reaction savedReaction = reactionRepository.save(newReaction);

            // Publish event CHỈ khi tạo reaction mới
            eventPublisher.publishEvent(new ReactionAddedEvent(
                    postId,
                    userId,
                    request.getType()
            ));

        } else if (existingReaction.getType() != request.getType()) {
            // ==================== CASE 2: ĐỔI REACTION TYPE ====================
            existingReaction.setType(request.getType());
            reactionRepository.save(existingReaction);
            // KHÔNG publish event khi chỉ đổi type

        } else {
            // ==================== CASE 3: UNLIKE (XÓA) ====================
            reactionRepository.delete(existingReaction);
            // KHÔNG publish event khi xóa
        }
    }

    /**
     * Lấy thống kê reaction của một bài viết
     * Sử dụng custom query tối ưu trong Repository
     */
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
}