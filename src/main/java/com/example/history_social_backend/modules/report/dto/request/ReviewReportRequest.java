package com.example.history_social_backend.modules.report.dto.request;

import com.example.history_social_backend.modules.report.domain.ReportStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReportRequest {

    @NotNull(message = "Status is required")
    private ReportStatus status;
}