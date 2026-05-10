package com.example.history_social_backend.modules.report.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "reports")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "reporter_id", nullable = false, columnDefinition = "uuid")
    UUID reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    ReportTargetType targetType;

    @Column(name = "target_id", nullable = false, columnDefinition = "uuid")
    UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 50)
    ReportReasonType reasonType;

    @Column(name = "reason_text", columnDefinition = "TEXT")
    String reasonText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    ReportStatus status = ReportStatus.PENDING;

    @Column(name = "reviewed_by", columnDefinition = "uuid")
    UUID reviewedBy;

    @Column(name = "reviewed_at")
    LocalDateTime reviewedAt;
}