package com.example.history_social_backend.modules.notification.service;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.notification.domain.Notification;
import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.dto.CreateNotificationRequest;
import com.example.history_social_backend.modules.notification.dto.NotificationResponse;
import com.example.history_social_backend.modules.notification.mapper.NotificationMapper;
import com.example.history_social_backend.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        if (request.getRecipientId() == null) {
            return null;
        }

        if (request.getActorId() != null && request.getActorId().equals(request.getRecipientId())) {
            return null;
        }

        Notification notification = Notification.builder()
                .recipientId(request.getRecipientId())
                .actorId(request.getActorId())
                .type(request.getType())
                // .title(request.getTitle())
                .content(request.getMessage())
                .referenceId(request.getTargetId())
                // .targetType(request.getTargetType())
                .isRead(false)
                .build();

        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationResponse createNotification(
            UUID recipientId,
            UUID actorId,
            NotificationType type,
            String title,
            String message,
            UUID targetId,
            String targetType) {
        return createNotification(CreateNotificationRequest.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type)
                .title(title)
                .message(message)
                .targetId(targetId)
                .targetType(targetType)
                .build());
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(int page, int size) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<NotificationResponse> responsePage = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUserId, pageable)
                .map(notificationMapper::toResponse);

        return PageResponse.<NotificationResponse>builder()
                .currentPage(responsePage.getNumber())
                .size(responsePage.getSize())
                .totalPages(responsePage.getTotalPages())
                .totalElements(responsePage.getTotalElements())
                .content(responsePage.getContent())
                .build();
    }

    @Transactional(readOnly = true)
    public long countUnreadNotifications() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUserId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow();

        if (!notification.getRecipientId().equals(currentUserId)) {
            return;
        }
        notification.markAsRead();

        // notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}