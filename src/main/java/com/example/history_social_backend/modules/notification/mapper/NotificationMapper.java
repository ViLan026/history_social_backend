package com.example.history_social_backend.modules.notification.mapper;

import com.example.history_social_backend.modules.notification.domain.Notification;
import com.example.history_social_backend.modules.notification.dto.NotificationResponse;
import com.example.history_social_backend.modules.notification.dto.SystemNotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Thay thế cho hàm toResponse thủ công trong Service
    NotificationResponse toResponse(Notification notification);

    // (Tùy chọn thêm) Hỗ trợ map từ Request tạo thông báo hệ thống sang Entity
    @Mapping(target = "actorId", ignore = true)
    @Mapping(target = "referenceId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Notification toSystemNotification(SystemNotificationRequest request);
}