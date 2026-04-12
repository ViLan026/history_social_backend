package com.example.history_social_backend.modules.report.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    UUID id;

    @JoinColumn(name = "reporter_id", nullable = false)
    UUID reporterId;

    @JoinColumn(name = "post_id", nullable = false)
    UUID postId;

    @Column(columnDefinition = "TEXT")
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportStatus status;

}