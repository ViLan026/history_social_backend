package com.example.history_social_backend.modules.report.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.modules.report.domain.ReportReasonType;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {

    UUID id;
    UUID reporterId;
    ReportTargetType targetType;
    UUID targetId;
    ReportReasonType reasonType;
    String reasonText;
    ReportStatus status;
    UUID reviewedBy;
    LocalDateTime reviewedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;    
}