package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestPendingReportResponse {

    private UUID reportId;
    private String targetType;
    private UUID targetId;
    private String reasonType;
    private String reasonText;
    private UUID reporterId;
    private LocalDateTime createdAt;
}