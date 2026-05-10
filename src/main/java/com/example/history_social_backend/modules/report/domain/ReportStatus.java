package com.example.history_social_backend.modules.report.domain;

public enum ReportStatus {

    PENDING,      // Đang chờ admin xử lý
    RESOLVED,     // Đã xác nhận vi phạm và xử lý
    DISMISSED     // Báo cáo không hợp lệ
}