package com.example.history_social_backend.modules.report.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.comment.service.CommentService;
import com.example.history_social_backend.modules.post.service.PostQueryService;
import com.example.history_social_backend.modules.report.domain.Report;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;
import com.example.history_social_backend.modules.report.dto.request.CreateReportRequest;
import com.example.history_social_backend.modules.report.dto.request.ReviewReportRequest;
import com.example.history_social_backend.modules.report.dto.response.ModerationReportResponse;
import com.example.history_social_backend.modules.report.dto.response.MyReportResponse;
import com.example.history_social_backend.modules.report.dto.response.ReportResponse;
import com.example.history_social_backend.modules.report.dto.response.TargetPreviewResponse;
import com.example.history_social_backend.modules.report.mapper.ReportMapper;
import com.example.history_social_backend.modules.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final PostQueryService postQueryService;
    private final CommentService commentService;

    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        // check xem bài viết hoặc comment bị báo cáo có tồn tại không
        validateTargetExists(request.getTargetType(), request.getTargetId());

        // Check duplicate report
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                currentUserId,
                request.getTargetType(),
                request.getTargetId())) {
            throw new AppException(ErrorCode.REPORT_ALREADY_EXISTS);
        }

        // Create report
        Report report = new Report();
        report.setReporterId(currentUserId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReasonType(request.getReasonType());
        report.setReasonText(request.getReasonText());
        report.setStatus(ReportStatus.PENDING);

        Report savedReport = reportRepository.save(report);
        log.info("User {} created report {} for {} {}",
                currentUserId, savedReport.getId(), request.getTargetType(), request.getTargetId());

        return reportMapper.toReportResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public PageResponse<MyReportResponse> getMyReports(int page, int size) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Lấy các báo cáo do user tạo ra
        Page<Report> myCreatedReports = reportRepository.findByReporterId(currentUserId, pageable);

        // Lấy các báo cáo về nội dung của user
        // Page<Report> reportsOnMyContent =
        // reportRepository.findByTargetId(currentUserId, pageable);

        // Combine và xử lý
        Page<MyReportResponse> responsePage = myCreatedReports
                .map(report -> buildMyReportResponse(report, currentUserId));

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public PageResponse<ModerationReportResponse> getPendingReports(
            int page,
            int size,
            ReportTargetType targetType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<Report> reports;

        if (targetType == null) {
            reports = reportRepository.findByStatus(ReportStatus.PENDING, pageable);
        } else {
            reports = reportRepository.findByStatusAndTargetType(
                    ReportStatus.PENDING,
                    targetType,
                    pageable);
        }

        Page<ModerationReportResponse> responsePage = reports.map(this::buildModerationResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional
    public ReportResponse reviewReport(UUID reportId, ReviewReportRequest request) {
        UUID adminUserId = SecurityUtils.getCurrentUserId();

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new AppException(ErrorCode.REPORT_ALREADY_REVIEWED);
        }

        if (request.getStatus() == ReportStatus.RESOLVED) {
            handleConfirmedViolation(report);
        }

        report.setStatus(request.getStatus());
        report.setReviewedBy(adminUserId);
        report.setReviewedAt(LocalDateTime.now());

        Report updatedReport = reportRepository.save(report);

        return reportMapper.toReportResponse(updatedReport);
    }

    private void handleConfirmedViolation(Report report) {
        switch (report.getTargetType()) {
            case POST -> postQueryService.rejectPostByAdmin(report.getTargetId());
            case COMMENT -> commentService.deleteCommentByAdmin(report.getTargetId());
        }
    }

    @Transactional(readOnly = true)
    public long getReportCount(ReportTargetType targetType, UUID targetId) {
        return reportRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    private void validateTargetExists(ReportTargetType targetType, UUID targetId) {
        switch (targetType) {
            case POST -> {
                if (!postQueryService.existsById(targetId)) {
                    throw new AppException(ErrorCode.POST_NOT_FOUND);
                }
            }
            case COMMENT -> {
                if (!commentService.existsById(targetId)) {
                    throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
                }
            }
        }
    }

    private ModerationReportResponse buildModerationResponse(Report report) {
        ReportResponse reportResponse = reportMapper.toReportResponse(report);
        TargetPreviewResponse targetPreview = buildTargetPreview(report.getTargetType(), report.getTargetId());

        return ModerationReportResponse.builder()
                .report(reportResponse)
                .targetPreview(targetPreview)
                .build();
    }

    private TargetPreviewResponse buildTargetPreview(ReportTargetType targetType, UUID targetId) {
        switch (targetType) {
            case POST -> {
                return postQueryService.getPostPreviewForModeration(targetId);
            }
            case COMMENT -> {
                return commentService.getCommentPreviewForModeration(targetId);
            }
            default -> {
                return TargetPreviewResponse.builder()
                        .id(targetId)
                        .content("Unknown target type")
                        .build();
            }
        }
    }

    private MyReportResponse buildMyReportResponse(Report report, UUID currentUserId) {
        // Kiểm tra xem đây có phải báo cáo về nội dung của user không
        boolean isMyContentReported = false;
        String targetStatus = "ACTIVE";
        String targetContentPreview = null;
        boolean targetExists = true;

        try {
            switch (report.getTargetType()) {
                case POST -> {
                    var postPreview = postQueryService.getPostPreviewForModeration(report.getTargetId());

                    if (postPreview == null) {
                        targetExists = false;
                        targetStatus = "DELETED";
                    } else {
                        targetExists = true;
                        isMyContentReported = postPreview.getAuthorId().equals(currentUserId);

                        if (Boolean.TRUE.equals(postPreview.getIsHiddenByAdmin())) {
                            targetStatus = "HIDDEN_BY_ADMIN";
                        } else if (Boolean.TRUE.equals(postPreview.getIsHiddenByAuthor())) {
                            targetStatus = "HIDDEN_BY_AUTHOR";
                        } else if (Boolean.TRUE.equals(postPreview.getIsDeleted())) {
                            targetStatus = "DELETED";
                            targetExists = false;
                        }

                        // Người báo cáo chỉ xem được preview nếu bài viết vẫn active
                        // Chủ bài viết luôn xem được lý do báo cáo
                        if (isMyContentReported || "ACTIVE".equals(targetStatus)) {
                            targetContentPreview = postPreview.getContent();
                        }
                    }
                }
                case COMMENT -> {
                    var commentPreview = commentService.getCommentPreviewForModeration(report.getTargetId());

                    if (commentPreview == null) {
                        targetExists = false;
                        targetStatus = "DELETED";
                    } else {
                        targetExists = true;
                        isMyContentReported = commentPreview.getAuthorId().equals(currentUserId);

                        if (Boolean.TRUE.equals(commentPreview.getIsHiddenByAdmin())) {
                            targetStatus = "HIDDEN_BY_ADMIN";
                        } else if (Boolean.TRUE.equals(commentPreview.getIsHiddenByAuthor())) {
                            targetStatus = "HIDDEN_BY_AUTHOR";
                        } else if (Boolean.TRUE.equals(commentPreview.getIsDeleted())) {
                            targetStatus = "DELETED";
                            targetExists = false;
                        }

                        if (isMyContentReported || "ACTIVE".equals(targetStatus)) {
                            targetContentPreview = commentPreview.getContent();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get target preview for report {}: {}", report.getId(), e.getMessage());
            targetExists = false;
            targetStatus = "DELETED";
        }

        return MyReportResponse.builder()
                .id(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reasonType(report.getReasonType())
                .reasonText(report.getReasonText())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .targetExists(targetExists)
                .targetStatus(targetStatus)
                .targetContentPreview(targetContentPreview)
                .isMyContentReported(isMyContentReported)
                .build();
    }
}