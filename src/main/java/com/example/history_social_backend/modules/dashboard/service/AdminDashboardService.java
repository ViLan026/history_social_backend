package com.example.history_social_backend.modules.dashboard.service;

import com.example.history_social_backend.modules.dashboard.dto.response.*;
import com.example.history_social_backend.modules.dashboard.repository.DashboardQueryRepository;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.user.domain.AccountStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final DashboardQueryRepository dashboardQueryRepository;

    //  Tổng quan dashboard
public DashboardOverviewResponse getOverview() {
    return DashboardOverviewResponse.builder()
            // Users
            .totalUsers(dashboardQueryRepository.countAllUsers())
            .activeUsers(dashboardQueryRepository.countUsersByStatus(AccountStatus.ACTIVE))
            .inactiveUsers(dashboardQueryRepository.countUsersByStatus(AccountStatus.INACTIVE))

            // Posts
            .totalPosts(dashboardQueryRepository.countAllPosts())
            .publishedPosts(dashboardQueryRepository.countPostsByStatus(PostStatus.PUBLISHED))
            .draftPosts(dashboardQueryRepository.countPostsByStatus(PostStatus.DRAFT))
            .hiddenPosts(dashboardQueryRepository.countPostsByStatus(PostStatus.HIDDEN))
            .flaggedPosts(dashboardQueryRepository.countPostsByStatus(PostStatus.FLAGGED))
            .rejectedPosts(dashboardQueryRepository.countPostsByStatus(PostStatus.REJECTED))

            // Reports
            .pendingReports(dashboardQueryRepository.countReportsByStatus(ReportStatus.PENDING))
            .resolvedReports(dashboardQueryRepository.countReportsByStatus(ReportStatus.RESOLVED))
            .dismissedReports(dashboardQueryRepository.countReportsByStatus(ReportStatus.DISMISSED))

            // Interactions
            .totalComments(dashboardQueryRepository.countAllComments())
            .totalReactions(dashboardQueryRepository.countAllReactions())
            .totalBookmarks(dashboardQueryRepository.countAllBookmarks())
            .totalFollows(dashboardQueryRepository.countAllFollows())
            .build();
}
    // Thống kê bài viết theo trạng thái
    public List<CountByStatusResponse> getPostStatusStats() {
        List<Object[]> rows = dashboardQueryRepository.countPostsGroupByStatus();
        return rows.stream()
                .map(row -> CountByStatusResponse.builder()
                        .name(row[0] != null ? row[0].toString() : "UNKNOWN")
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Thống kê report theo trạng thái
    public List<CountByStatusResponse> getReportStatusStats() {
        List<Object[]> rows = dashboardQueryRepository.countReportsGroupByStatus();
        return rows.stream()
                .map(row -> CountByStatusResponse.builder()
                        .name(row[0] != null ? row[0].toString() : "UNKNOWN")
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Thống kê report theo lý do
    public List<CountByTypeResponse> getReportReasonStats() {
        List<Object[]> rows = dashboardQueryRepository.countReportsGroupByReason();
        return rows.stream()
                .map(row -> CountByTypeResponse.builder()
                        .name(row[0] != null ? row[0].toString() : "UNKNOWN")
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Người dùng mới theo ngày
    public List<TimeSeriesStatResponse> getNewUsers(int days) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);

        Map<LocalDate, Long> dataMap = toDateCountMap(
                dashboardQueryRepository.countNewUsersByDay(from, to));

        return buildTimeSeries(LocalDate.now().minusDays(days - 1L), LocalDate.now(), dataMap);
    }

    // Bài viết mới theo ngày
    public List<TimeSeriesStatResponse> getNewPosts(int days) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);

        Map<LocalDate, Long> dataMap = toDateCountMap(
                dashboardQueryRepository.countNewPostsByDay(from, to));

        return buildTimeSeries(LocalDate.now().minusDays(days - 1L), LocalDate.now(), dataMap);
    }

    // Tương tác theo ngày
    public List<EngagementStatResponse> getEngagementStats(int days) {
        LocalDateTime from = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);

        Map<LocalDate, Long> commentMap = toDateCountMap(
                dashboardQueryRepository.countCommentsByDay(from, to));
        Map<LocalDate, Long> reactionMap = toDateCountMap(
                dashboardQueryRepository.countReactionsByDay(from, to));
        Map<LocalDate, Long> bookmarkMap = toDateCountMap(
                dashboardQueryRepository.countBookmarksByDay(from, to));
        Map<LocalDate, Long> followMap = toDateCountMap(
                dashboardQueryRepository.countFollowsByDay(from, to));

        LocalDate startDate = LocalDate.now().minusDays(days - 1L);
        LocalDate endDate = LocalDate.now();

        List<EngagementStatResponse> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.add(EngagementStatResponse.builder()
                    .date(date)
                    .comments(commentMap.getOrDefault(date, 0L))
                    .reactions(reactionMap.getOrDefault(date, 0L))
                    .bookmarks(bookmarkMap.getOrDefault(date, 0L))
                    .follows(followMap.getOrDefault(date, 0L))
                    .build());
        }
        return result;
    }

    // Top bài viết bị report nhiều nhất
    public List<TopReportedPostResponse> getTopReportedPosts(int limit) {
        List<Object[]> rows = dashboardQueryRepository.findTopReportedPosts(limit);
        return rows.stream()
                .map(row -> TopReportedPostResponse.builder()
                        .postId(toUUID(row[0]))
                        .title((String) row[1])
                        .authorId(toUUID(row[2]))
                        .status(row[3] != null ? row[3].toString() : null)
                        // .qualityScore(row[4] != null ? ((Number) row[4]).doubleValue() : null)
                        .createdAt((LocalDateTime) row[4])
                        .reportCount(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Report mới nhất cần xử lý
    public List<LatestPendingReportResponse> getLatestPendingReports(int limit) {
        List<Object[]> rows = dashboardQueryRepository.findLatestPendingReports(limit);
        return rows.stream()
                .map(row -> LatestPendingReportResponse.builder()
                        .reportId(toUUID(row[0]))
                        .targetType(row[1] != null ? row[1].toString() : null)
                        .targetId(toUUID(row[2]))
                        .reasonType(row[3] != null ? row[3].toString() : null)
                        .reasonText((String) row[4])
                        .reporterId(toUUID(row[5]))
                        .createdAt((LocalDateTime) row[6])
                        .build())
                .collect(Collectors.toList());
    }

    // Top tags phổ biến
    public List<TopTagResponse> getTopTags(int limit) {
        List<Object[]> rows = dashboardQueryRepository.findTopTags(limit);
        return rows.stream()
                .map(row -> TopTagResponse.builder()
                        .tagId(toUUID(row[0]))
                        .name((String) row[1])
                        .usageCount(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Thống kê reaction theo loại
    public List<ReactionStatResponse> getReactionStats() {
        List<Object[]> rows = dashboardQueryRepository.countReactionsGroupByType();
        return rows.stream()
                .map(row -> ReactionStatResponse.builder()
                        .name(row[0] != null ? row[0].toString() : "UNKNOWN")
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }


    private Map<LocalDate, Long> toDateCountMap(List<Object[]> rows) {
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            if (row[0] == null) continue;
            LocalDate date = toLocalDate(row[0]);
            long count = ((Number) row[1]).longValue();
            map.put(date, count);
        }
        return map;
    }


    private List<TimeSeriesStatResponse> buildTimeSeries(LocalDate startDate,
                                                          LocalDate endDate,
                                                          Map<LocalDate, Long> dataMap) {
        List<TimeSeriesStatResponse> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.add(TimeSeriesStatResponse.builder()
                    .date(date)
                    .count(dataMap.getOrDefault(date, 0L))
                    .build());
        }
        return result;
    }


    private UUID toUUID(Object obj) {
        if (obj == null) return null;
        if (obj instanceof UUID) return (UUID) obj;
        return UUID.fromString(obj.toString());
    }


    private LocalDate toLocalDate(Object obj) {
        if (obj instanceof LocalDate) return (LocalDate) obj;
        if (obj instanceof java.sql.Date) return ((java.sql.Date) obj).toLocalDate();
        return LocalDate.parse(obj.toString());
    }
}