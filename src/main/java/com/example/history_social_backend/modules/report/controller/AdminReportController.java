package com.example.history_social_backend.modules.report.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.report.dto.request.ReviewReportRequest;
import com.example.history_social_backend.modules.report.dto.response.ModerationReportResponse;
import com.example.history_social_backend.modules.report.dto.response.ReportResponse;
import com.example.history_social_backend.modules.report.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.ADMIN_REPORTS)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/pending")
    public ApiResponse<PageResponse<ModerationReportResponse>> getPendingReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<ModerationReportResponse> response = reportService.getPendingReports(page, size);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/review")
    public ApiResponse<ReportResponse> reviewReport(
        @PathVariable UUID id,
        @Valid @RequestBody ReviewReportRequest request
    ) {
        ReportResponse response = reportService.reviewReport(id, request);
        return ApiResponse.success("Đã xét duyệt báo cáo thành công", response);
    }
}