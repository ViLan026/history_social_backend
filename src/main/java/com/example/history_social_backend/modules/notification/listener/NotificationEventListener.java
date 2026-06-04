package com.example.history_social_backend.modules.notification.listener;

import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.event.CommentCreatedEvent;
import com.example.history_social_backend.modules.notification.event.PostStatusChangedEvent;
import com.example.history_social_backend.modules.notification.event.ReactionCreatedEvent;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventListener {

    NotificationService notificationService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        notificationService.createNotification(
                event.getReceiverId(),
                event.getSenderId(),
                NotificationType.COMMENT,
                "Bình luận mới",
                event.getSenderName() + " đã bình luận về bài viết của bạn",
                event.getPostId(),
                "POST"
        );
    }

    @EventListener
    public void handleReactionCreated(ReactionCreatedEvent event) {
        notificationService.createNotification(
                event.getReceiverId(),
                event.getSenderId(),
                NotificationType.REACTION,
                "Tương tác mới",
                event.getSenderName() + " đã bày tỏ cảm xúc về bài viết của bạn",
                event.getPostId(),
                "POST"
        );
    }

    @EventListener
    public void handlePostStatusChanged(PostStatusChangedEvent event) {
        notificationService.createNotification(
                event.getReceiverId(),
                null,
                NotificationType.POST,
                "Trạng thái bài viết đã thay đổi",
                "Bài viết của bạn đã được cập nhật trạng thái: " + event.getStatus(),
                event.getPostId(),
                "POST"
        );
    }
}