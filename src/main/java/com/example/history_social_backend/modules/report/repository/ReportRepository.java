package com.example.history_social_backend.modules.report.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.report.domain.Report;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import com.example.history_social_backend.modules.report.domain.ReportTargetType;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    boolean existsByReporterIdAndTargetTypeAndTargetId(UUID reporterId, ReportTargetType targetType, UUID targetId);

    long countByTargetTypeAndTargetId(ReportTargetType targetType, UUID targetId);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, UUID targetId);

    Page<Report> findByReporterId(UUID reporterId, Pageable pageable);

    Page<Report> findByTargetId(UUID targetId, Pageable pageable);

    Page<Report> findByStatusAndTargetType(ReportStatus status, ReportTargetType targetType, Pageable pageable);
}