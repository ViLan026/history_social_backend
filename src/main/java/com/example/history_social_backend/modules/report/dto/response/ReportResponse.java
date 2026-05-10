package com.example.history_social_backend.modules.report.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.modules.report.domain.ReportReasonType;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponse {

    private UUID id;

    private UUID reporterId;

    private ReportTargetType targetType;

    private UUID targetId;

    private ReportReasonType reasonType;

    private String reasonText;

    private ReportStatus status;

    private UUID reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;
}