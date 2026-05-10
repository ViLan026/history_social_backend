package com.example.history_social_backend.modules.report.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.report.dto.request.CreateReportRequest;
import com.example.history_social_backend.modules.report.dto.request.ReviewReportRequest;
import com.example.history_social_backend.modules.report.dto.response.ReportResponse;
import com.example.history_social_backend.modules.report.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest request
    ) {

        return ApiResponse.success(
                "Report created successfully",
                reportService.createReport(request)
        );
    }

    @GetMapping("/pending")
    public ApiResponse<PageResponse<ReportResponse>> getPendingReports(
            Pageable pageable
    ) {

        return ApiResponse.success(
                reportService.getPendingReports(pageable)
        );
    }

    @PatchMapping("/{reportId}/review")
    public ApiResponse<ReportResponse> reviewReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody ReviewReportRequest request
    ) {

        return ApiResponse.success(
                "Report reviewed successfully",
                reportService.reviewReport(reportId, request)
        );
    }
}