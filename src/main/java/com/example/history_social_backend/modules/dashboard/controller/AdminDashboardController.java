package com.example.history_social_backend.modules.dashboard.controller;

import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.dashboard.dto.response.*;
import com.example.history_social_backend.modules.dashboard.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Tất cả API chỉ dành cho ADMIN, chỉ đọc thống kê, không thao tác ghi.
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // Tổng quan dashboard
    // GET /admin/dashboard/overview
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        DashboardOverviewResponse data = adminDashboardService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Thống kê bài viết theo trạng thái
    // GET /admin/dashboard/post-status-stats
    @GetMapping("/post-status-stats")
    public ResponseEntity<ApiResponse<List<CountByStatusResponse>>> getPostStatusStats() {
        List<CountByStatusResponse> data = adminDashboardService.getPostStatusStats();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Thống kê report theo trạng thái
    // GET /admin/dashboard/report-status-stats
    @GetMapping("/report-status-stats")
    public ResponseEntity<ApiResponse<List<CountByStatusResponse>>> getReportStatusStats() {
        List<CountByStatusResponse> data = adminDashboardService.getReportStatusStats();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Thống kê report theo lý do
    // GET /admin/dashboard/report-reason-stats
    @GetMapping("/report-reason-stats")
    public ResponseEntity<ApiResponse<List<CountByTypeResponse>>> getReportReasonStats() {
        List<CountByTypeResponse> data = adminDashboardService.getReportReasonStats();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Người dùng mới theo ngày
    // GET /admin/dashboard/new-users?days=7
    @GetMapping("/new-users")
    public ResponseEntity<ApiResponse<List<TimeSeriesStatResponse>>> getNewUsers(
            @RequestParam(defaultValue = "7") int days) {

        days = clampDays(days);
        List<TimeSeriesStatResponse> data = adminDashboardService.getNewUsers(days);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Bài viết mới theo ngày
    // GET /admin/dashboard/new-posts?days=7
    @GetMapping("/new-posts")
    public ResponseEntity<ApiResponse<List<TimeSeriesStatResponse>>> getNewPosts(
            @RequestParam(defaultValue = "7") int days) {

        days = clampDays(days);
        List<TimeSeriesStatResponse> data = adminDashboardService.getNewPosts(days);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Tương tác theo ngày
    // GET /admin/dashboard/engagement-stats?days=7
    @GetMapping("/engagement-stats")
    public ResponseEntity<ApiResponse<List<EngagementStatResponse>>> getEngagementStats(
            @RequestParam(defaultValue = "7") int days) {

        days = clampDays(days);
        List<EngagementStatResponse> data = adminDashboardService.getEngagementStats(days);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Top bài viết bị report nhiều nhất
    // GET /admin/dashboard/top-reported-posts?limit=10
    @GetMapping("/top-reported-posts")
    public ResponseEntity<ApiResponse<List<TopReportedPostResponse>>> getTopReportedPosts(
            @RequestParam(defaultValue = "10") int limit) {

        limit = clampLimit(limit);
        List<TopReportedPostResponse> data = adminDashboardService.getTopReportedPosts(limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Report mới nhất cần xử lý
    // GET /admin/dashboard/latest-pending-reports?limit=10
    @GetMapping("/latest-pending-reports")
    public ResponseEntity<ApiResponse<List<LatestPendingReportResponse>>> getLatestPendingReports(
            @RequestParam(defaultValue = "10") int limit) {

        limit = clampLimit(limit);
        List<LatestPendingReportResponse> data = adminDashboardService.getLatestPendingReports(limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Top tags phổ biến
    // GET /admin/dashboard/top-tags?limit=10
    @GetMapping("/top-tags")
    public ResponseEntity<ApiResponse<List<TopTagResponse>>> getTopTags(
            @RequestParam(defaultValue = "10") int limit) {

        limit = clampLimit(limit);
        List<TopTagResponse> data = adminDashboardService.getTopTags(limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Thống kê reaction theo loại
    // GET /admin/dashboard/reaction-stats
    @GetMapping("/reaction-stats")
    public ResponseEntity<ApiResponse<List<ReactionStatResponse>>> getReactionStats() {
        List<ReactionStatResponse> data = adminDashboardService.getReactionStats();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Giới hạn days trong khoảng [1, 365].
    private int clampDays(int days) {
        if (days < 1)
            return 1;
        if (days > 365)
            return 365;
        return days;
    }

    // Giới hạn limit trong khoảng [1, 50].
    private int clampLimit(int limit) {
        if (limit < 1)
            return 1;
        if (limit > 50)
            return 50;
        return limit;
    }
}