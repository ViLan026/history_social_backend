package com.example.history_social_backend.modules.notification.service;

import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.comment.service.CommentService;
import com.example.history_social_backend.modules.notification.domain.Notification;
import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.dto.CreateNotificationRequest;
import com.example.history_social_backend.modules.notification.dto.NotificationResponse;
import com.example.history_social_backend.modules.notification.dto.NotificationUserResponse;
import com.example.history_social_backend.modules.notification.mapper.NotificationMapper;
import com.example.history_social_backend.modules.notification.repository.NotificationRepository;
import com.example.history_social_backend.modules.user.dto.response.ProfileResponse;
import com.example.history_social_backend.modules.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserQueryService userQueryService;
    CommentService commentQueryService;

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
                .content(request.getMessage())
                .referenceId(request.getTargetId())
                .read(false)
                .build();

        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationResponse createNotification(
            UUID recipientId,
            UUID actorId,
            NotificationType type,
            String message,
            UUID targetId) {
        return createNotification(CreateNotificationRequest.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type)
                .message(message)
                .targetId(targetId)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<NotificationUserResponse> getMyNotifications(Pageable pageable) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Page<Notification> notificationPage = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(
                currentUserId,
                pageable);

        Set<UUID> actorIds = notificationPage.getContent()
                .stream()
                .map(Notification::getActorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, ProfileResponse> userMap = userQueryService.getUsergetUserFollowInfoMap(actorIds);

        return notificationPage.map(notification -> {

            NotificationUserResponse response = notificationMapper.toUserResponse(notification);

            ProfileResponse actor = userMap.get(notification.getActorId());

            if (actor != null) {
                response.setDisplayName(actor.getDisplayName());
                response.setAvatarUrl(actor.getAvatarUrl());
            }

            enrichNavigationData(notification, response);

            return response;
        });
    }

    private void enrichNavigationData(
            Notification notification,
            NotificationUserResponse response) {
        UUID referenceId = notification.getReferenceId();

        if (referenceId == null || notification.getType() == null) {
            return;
        }

        NotificationType type = notification.getType();

        switch (type) {
            case COMMENT, REPLY -> {
                response.setCommentId(referenceId);
                response.setPostId(commentQueryService.getPostIdByCommentId(referenceId));
            }

            case REACTION, POST -> response.setPostId(referenceId);

            case REPORT -> response.setReportId(referenceId);

            default -> {
            }
        }
    }

    @Transactional(readOnly = true)
    public long countUnreadNotifications() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return notificationRepository.countByRecipientIdAndReadFalse(currentUserId);
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