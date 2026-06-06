package com.example.history_social_backend.modules.notification.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.notification.dto.NotificationUserResponse;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationUserResponse>> getMyNotifications(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<NotificationUserResponse> page = notificationService.getMyNotifications(pageable);

        return ApiResponse.success(PageResponse.from(page));
    }

    // Đánh dấu 1 thông báo đã đọc
    @PutMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.success("Notification marked as read");
    }


    // // Lấy số lượng thông báo chưa đọc
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
    long count = notificationService.countUnreadNotifications();
    return ApiResponse.success(count);
    }

    // // API Admin - Gửi thông báo hệ thống (đến 1 user hoặc tất cả)
    // @PostMapping("/system")
    // @ResponseStatus(HttpStatus.CREATED)
    // public ApiResponse<Void> sendSystemNotification(@RequestBody
    // SystemNotificationRequest request) {
    // notificationService.createSystemNotification(request);
    // return ApiResponse.success("System notification sent successfully");
    // }
}