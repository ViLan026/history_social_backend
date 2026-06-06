package com.example.history_social_backend.modules.notification.event;

import com.example.history_social_backend.modules.report.domain.ReportStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResolvedEvent {

    UUID reportId;
    UUID actorId;
    UUID recipientId;
    ReportStatus status;
    String adminNote;
}