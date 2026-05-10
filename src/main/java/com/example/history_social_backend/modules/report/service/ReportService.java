package com.example.history_social_backend.modules.report.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.report.domain.Report;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.report.dto.request.CreateReportRequest;
import com.example.history_social_backend.modules.report.dto.request.ReviewReportRequest;
import com.example.history_social_backend.modules.report.dto.response.ReportResponse;
import com.example.history_social_backend.modules.report.mapper.ReportMapper;
import com.example.history_social_backend.modules.report.repository.ReportRepository;
import com.example.history_social_backend.core.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportResponse createReport(CreateReportRequest request) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        boolean alreadyReported =
                reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                        currentUserId,
                        request.getTargetType(),
                        request.getTargetId()
                );

        if (alreadyReported) {
            throw new AppException(ErrorCode.REPORT_ALREADY_EXISTS);
        }

        Report report = new Report();

        report.setReporterId(currentUserId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReasonType(request.getReasonType());
        report.setReasonText(request.getReasonText());
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);

        return reportMapper.toResponse(report);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getPendingReports(Pageable pageable) {

        Page<ReportResponse> pageData = reportRepository
                .findByStatus(ReportStatus.PENDING, pageable)
                .map(reportMapper::toResponse);

        return PageResponse.from(pageData);
    }

    public ReportResponse reviewReport(
            UUID reportId,
            ReviewReportRequest request
    ) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.REPORT_NOT_FOUND)
                );

        report.setStatus(request.getStatus());
        report.setReviewedBy(SecurityUtils.getCurrentUserId());
        report.setReviewedAt(LocalDateTime.now());

        reportRepository.save(report);

        return reportMapper.toResponse(report);
    }
}