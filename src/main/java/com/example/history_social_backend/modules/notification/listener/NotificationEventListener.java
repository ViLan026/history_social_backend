package com.example.history_social_backend.modules.notification.listener;

import com.example.history_social_backend.common.event.CommentCreatedEvent;
import com.example.history_social_backend.common.event.FollowCreatedEvent;
import com.example.history_social_backend.common.event.ReactionAddedEvent;
import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

/**
 * Event Listener cho FR-21
 * Nghe các event tương tác và tạo thông báo
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final PostRepository postRepository;

    /**
     * Xử lý event ReactionAddedEvent
     * Async + sau khi transaction commit thành công
     */
    @Async
    @TransactionalEventListener
    public void handleReactionAdded(ReactionAddedEvent event) {
        Post post = postRepository.findById(event.postId()).orElse(null);
        if (post == null) return;

        UUID recipientId = post.getAuthorId();
        UUID actorId = event.userId();

        // Check self-interaction: user tự like bài của mình → không tạo thông báo
        if (actorId.equals(recipientId)) {
            return;
        }

        notificationService.createInteractionNotification(
                actorId,
                recipientId,
                event.postId(),           // reference = post để FE navigate
                NotificationType.LIKE,
                null
        );
    }

    /**
     * Xử lý event CommentCreatedEvent
     */
    @Async
    @TransactionalEventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        Post post = postRepository.findById(event.postId()).orElse(null);
        if (post == null) return;

        UUID recipientId = post.getAuthorId();
        UUID actorId = event.authorId();

        // Check self-interaction: user tự comment bài của mình → không tạo thông báo
        if (actorId.equals(recipientId)) {
            return;
        }

        notificationService.createInteractionNotification(
                actorId,
                recipientId,
                event.postId(),           // reference = post
                NotificationType.COMMENT,
                null
        );
    }

    /**
     * Xử lý event FollowCreatedEvent
     */
    @Async
    @TransactionalEventListener
    public void handleFollowCreated(FollowCreatedEvent event) {
        UUID actorId = event.followerId();
        UUID recipientId = event.followedId();

        // Check self-interaction (dù hiếm xảy ra)
        if (actorId.equals(recipientId)) {
            return;
        }

        notificationService.createInteractionNotification(
                actorId,
                recipientId,
                null,                     // follow không cần reference
                NotificationType.FOLLOW,
                null
        );
    }
}