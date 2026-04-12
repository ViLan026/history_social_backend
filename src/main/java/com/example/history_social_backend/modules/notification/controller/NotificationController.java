package com.example.history_social_backend.modules.notification.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.notification.dto.NotificationResponse;
import com.example.history_social_backend.modules.notification.dto.SystemNotificationRequest;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        UUID currentUserId = UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());

        Page<NotificationResponse> page = notificationService.getNotifications(currentUserId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    // Đánh dấu 1 thông báo đã đọc
    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.success("Notification marked as read");
    }

    // Đánh dấu TẤT CẢ thông báo đã đọc
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("All notifications marked as read");
    }

    // Lấy số lượng thông báo chưa đọc
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ApiResponse.success(count);
    }

    // API Admin - Gửi thông báo hệ thống (đến 1 user hoặc tất cả)
    @PostMapping("/system")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> sendSystemNotification(@RequestBody SystemNotificationRequest request) {
        notificationService.createSystemNotification(request);
        return ApiResponse.success("System notification sent successfully");
    }
}