package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {

    // Users
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    // Posts
    private long totalPosts;
    private long publishedPosts;
    private long draftPosts;
    private long hiddenPosts;
    private long flaggedPosts;
    private long rejectedPosts;

    // Reports
    private long pendingReports;
    private long resolvedReports;
    private long dismissedReports;

    // Interactions
    private long totalComments;
    private long totalReactions;
    private long totalBookmarks;
    private long totalFollows;
}