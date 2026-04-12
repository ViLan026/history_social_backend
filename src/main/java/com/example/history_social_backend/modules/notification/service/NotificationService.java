package com.example.history_social_backend.modules.notification.service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.notification.domain.Notification;
import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.dto.NotificationResponse;
import com.example.history_social_backend.modules.notification.dto.SystemNotificationRequest;
import com.example.history_social_backend.modules.notification.mapper.NotificationMapper;
import com.example.history_social_backend.modules.notification.repository.NotificationRepository;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    // Tạo thông báo tương tác (gọi từ EventListener)
    @Transactional
    public void createInteractionNotification(UUID actorId, UUID recipientId,
            UUID referenceId, NotificationType type, String content) {
        if (actorId.equals(recipientId)) {
            return; // Self-interaction
        }

        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setActorId(actorId);
        notification.setReferenceId(referenceId);
        notification.setType(type);
        notification.setContent(content);
        // notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    // Tạo thông báo hệ thống (chỉ ADMIN)
    @Transactional
    public void createSystemNotification(SystemNotificationRequest request) {
        if (!isCurrentUserAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (request.getRecipientId() != null) {
            // Gửi cho 1 user cụ thể (Sử dụng mapper để code gọn hơn)
            Notification notification = notificationMapper.toSystemNotification(request);
            notificationRepository.save(notification);
        } else {
            // Gửi cho TẤT CẢ user
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty())
                return;

            List<Notification> notifications = allUsers.stream().map(user -> {
                Notification n = notificationMapper.toSystemNotification(request);
                n.setRecipientId(user.getId());
                return n;
            }).toList();

            notificationRepository.saveAll(notifications);
        }
    }

    // Lấy danh sách thông báo của user hiện tại (phân trang)
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(UUID recipientId, Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);

        return page.map(notificationMapper::toResponse);
    }

    // Đánh dấu 1 thông báo đã đọc
    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        UUID currentUserId = getCurrentUserId();
        if (!notification.getRecipientId().equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    // Đánh dấu TẤT CẢ thông báo đã đọc (bulk update)
    @Transactional
    public void markAllAsRead() {
        UUID currentUserId = getCurrentUserId();
        notificationRepository.markAllAsRead(currentUserId);
    }

    // Đếm số thông báo chưa đọc
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UUID currentUserId = getCurrentUserId();
        return notificationRepository.countUnreadByRecipientId(currentUserId);
    }

    private UUID getCurrentUserId() {
        return UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

}