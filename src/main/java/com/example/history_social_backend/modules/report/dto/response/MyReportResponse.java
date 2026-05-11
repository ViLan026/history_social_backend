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
public class MyReportResponse {

    UUID id;
    ReportTargetType targetType;
    UUID targetId;
    ReportReasonType reasonType;
    String reasonText;
    ReportStatus status;
    LocalDateTime createdAt;
    
    // Thông tin về target (nếu còn tồn tại)
    Boolean targetExists;
    String targetStatus; // "ACTIVE", "HIDDEN_BY_ADMIN", "HIDDEN_BY_AUTHOR", "DELETED"
    String targetContentPreview; // Preview nội dung (nếu có quyền xem)
    
    // Chỉ hiển thị cho chủ bài viết bị báo cáo
    Boolean isMyContentReported; // true nếu đây là báo cáo về nội dung của user
}