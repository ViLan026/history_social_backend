package com.example.history_social_backend.modules.post.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.history_social_backend.modules.follow.service.FollowService;
import com.example.history_social_backend.modules.post.domain.Post;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedRankingService {
    private final FollowService followService;

    // FINAL SCORE WEIGHTS
    public static final double POPULARITY_WEIGHT = 0.35;
    public static final double RECENCY_WEIGHT = 0.30;
    public static final double AFFINITY_WEIGHT = 0.25;
    // public static final double QUALITY_WEIGHT = 0.10;

    // POPULARITY WEIGHTS
    public static final double REACTION_WEIGHT = 1.0;
    public static final double COMMENT_WEIGHT = 4.0;
    public static final double BOOKMARK_WEIGHT = 6.0;

    // AFFINITY
    public static final double FOLLOWING_BONUS = 1.0;

    // RECENCY
    public static final double RECENCY_DECAY_HOURS = 24.0;

    protected double calculateRecency(Post post) {

        LocalDateTime createdAt = post.getCreatedAt();
        long hours = Math.max(1, Duration.between(createdAt, LocalDateTime.now()).toHours());
        return 1.0 / (hours / RECENCY_DECAY_HOURS + 1);
    }

    // tính độ phổ biến của bài viết với
    // popularity = (reactions * reaction_weight) + (comments * comment_weight) +
    // (bookmarks * bookmark_weight)
    protected double calculatePopularity(Post post) {

        long reactions = post.getReactionCount() != null ? post.getReactionCount() : 0;
        long comments = post.getCommentCount() != null ? post.getCommentCount() : 0;
        long bookmarks = post.getBookmarkCount() != null ? post.getBookmarkCount() : 0;

        return (reactions * REACTION_WEIGHT) + (comments * COMMENT_WEIGHT) + (bookmarks * BOOKMARK_WEIGHT);
    }

    // tính độ thân thiết của người dùng với tác giả bài viết
    protected double calculateAffinity(Post post, UUID currentUserId) {

        if (currentUserId == null) {
            return 0;
        }
        boolean isFollowing = followService.isFollowing(currentUserId, post.getAuthorId());
        return isFollowing
                ? FOLLOWING_BONUS
                : 0;
    }

    // tính chất lượng bài viết với chất lượng được đánh giá bởi hệ thống trong quá
    // trình check fact
    // protected double calculateQuality(Post post) {
    // double quality = post.getQualityScore() != 0 ? post.getQualityScore() : 0.5;
    // quality = Math.max(0, quality);
    // quality = Math.min(1, quality);
    // return quality;
    // }

    protected double calculateFinalScore(double popularity, double recency, double affinity) {
        return (popularity * POPULARITY_WEIGHT) + (recency * RECENCY_WEIGHT) + (affinity * AFFINITY_WEIGHT);
    }

    protected double calculateFeedScore(Post post, UUID currentUserId) {

        double popularity = calculatePopularity(post);
        double recency = calculateRecency(post);
        double affinity = calculateAffinity(post, currentUserId);
        // double quality = calculateQuality(post);

        return calculateFinalScore(popularity, recency, affinity);
    }
}
