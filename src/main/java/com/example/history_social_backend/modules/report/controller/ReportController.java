package com.example.history_social_backend.modules.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.report.dto.request.CreateReportRequest;
import com.example.history_social_backend.modules.report.dto.response.MyReportResponse;
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
    public ApiResponse<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = reportService.createReport(request);
        return ApiResponse.success("Báo cáo đã được gửi thành công", response);
    }

    @GetMapping("/me")
    public ApiResponse<PageResponse<MyReportResponse>> getMyReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<MyReportResponse> response = reportService.getMyReports(page, size);
        return ApiResponse.success(response);
    }
}