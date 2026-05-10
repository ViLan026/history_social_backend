package com.example.history_social_backend.modules.report.dto.request;

import java.util.UUID;

import com.example.history_social_backend.modules.report.domain.ReportReasonType;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportRequest {

    @NotNull(message = "Target type is required")
    private ReportTargetType targetType;

    @NotNull(message = "Target id is required")
    private UUID targetId;

    @NotNull(message = "Reason type is required")
    private ReportReasonType reasonType;

    @NotBlank(message = "Reason text is required")
    @Size(min = 10, max = 1000, message = "Reason text must be between 10 and 1000 characters")
    private String reasonText;
}