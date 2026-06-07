package com.example.history_social_backend.modules.notification.listener;

import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.event.CommentCreatedEvent;
import com.example.history_social_backend.modules.notification.event.CommentHiddenByHsdEvent;
import com.example.history_social_backend.modules.notification.event.CommentRepliedEvent;
import com.example.history_social_backend.modules.notification.event.PostStatusChangedEvent;
import com.example.history_social_backend.modules.notification.event.ReactionCreatedEvent;
import com.example.history_social_backend.modules.notification.event.ReportCreatedEvent;
import com.example.history_social_backend.modules.notification.event.ReportResolvedEvent;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.report.domain.ReportStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventListener {

    NotificationService notificationService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        if (isInvalidEvent(event.getRecipientId(), event.getActorId())) {
            return;
        }

        notificationService.createNotification(
                event.getRecipientId(),
                event.getActorId(),
                NotificationType.COMMENT,
                event.getSenderName() + " đã bình luận về bài viết của bạn",
                event.getCommentId());

        log.info("Created COMMENT notification: actor={}, recipient={}, comment={}",
                event.getActorId(), event.getRecipientId(), event.getCommentId());
    }

    @EventListener
    public void handleCommentReplied(CommentRepliedEvent event) {
        if (isInvalidEvent(event.getRecipientId(), event.getActorId())) {
            return;
        }

        notificationService.createNotification(
                event.getRecipientId(),
                event.getActorId(),
                NotificationType.REPLY,
                event.getSenderName() + " đã trả lời bình luận của bạn",
                event.getReplyCommentId());

        log.info("Created REPLY notification: actor={}, recipient={}, replyComment={}",
                event.getActorId(), event.getRecipientId(), event.getReplyCommentId());
    }

    @EventListener
    public void handleReactionCreated(ReactionCreatedEvent event) {
        if (isInvalidEvent(event.getRecipientId(), event.getActorId())) {
            return;
        }

        notificationService.createNotification(
                event.getRecipientId(),
                event.getActorId(),
                NotificationType.REACTION,
                event.getSenderName() + " đã bày tỏ cảm xúc về bài viết của bạn",
                event.getPostId());

        log.info("Created REACTION notification: actor={}, recipient={}, post={}",
                event.getActorId(), event.getRecipientId(), event.getPostId());
    }

    @EventListener
    public void handlePostStatusChanged(PostStatusChangedEvent event) {
        if (event == null || event.getRecipientId() == null || event.getPostId() == null) {
            return;
        }

        PostStatus status = event.getStatus();

        if (status == PostStatus.PUBLISHED) {
            notificationService.createNotification(
                    event.getRecipientId(),
                    event.getActorId(),
                    NotificationType.POST,
                    "Bài viết của bạn đã được duyệt và hiển thị công khai",
                    event.getPostId());
        }

        if (status == PostStatus.HIDDEN) {
            notificationService.createNotification(
                    event.getRecipientId(),
                    event.getActorId(),
                    NotificationType.POST,
                    buildReasonMessage("Bài viết của bạn đã bị ẩn", event.getReason()),
                    event.getPostId());
        }

        if (status == PostStatus.FLAGGED) {
            notificationService.createNotification(
                    event.getRecipientId(),
                    event.getActorId(),
                    NotificationType.POST,
                    buildReasonMessage("Bài viết của bạn đã bị gắn cờ để admin xem xét", event.getReason()),
                    event.getPostId());
        }

        if (status == PostStatus.REJECTED) {
            notificationService.createNotification(
                    event.getRecipientId(),
                    event.getActorId(),
                    NotificationType.POST,
                    buildReasonMessage("Bài viết của bạn bị từ chối do không phù hợp", event.getReason()),
                    event.getPostId());
        }

        log.info("Created POST status notification: recipient={}, post={}, status={}",
                event.getRecipientId(), event.getPostId(), status);
    }

    @EventListener
    public void handleReportCreated(ReportCreatedEvent event) {
        if (event == null || event.getAdminId() == null || event.getReportId() == null) {
            return;
        }

        notificationService.createNotification(
                event.getAdminId(),
                event.getActorId(),
                NotificationType.REPORT,
                event.getSenderName() + " đã gửi một báo cáo mới cần xử lý",
                event.getReportId());

        log.info("Created REPORT notification for admin: actor={}, admin={}, report={}",
                event.getActorId(), event.getAdminId(), event.getReportId());
    }

    @EventListener
    public void handleReportResolved(ReportResolvedEvent event) {
        if (event == null || event.getRecipientId() == null || event.getReportId() == null) {
            return;
        }

        ReportStatus status = event.getStatus();

        String content;

        if (status == ReportStatus.RESOLVED) {
            content = buildReasonMessage("Báo cáo của bạn đã được admin xác nhận và xử lý", event.getAdminNote());
        } else if (status == ReportStatus.DISMISSED) {
            content = buildReasonMessage("Báo cáo của bạn đã được xem xét nhưng không đủ căn cứ xử lý",
                    event.getAdminNote());
        } else {
            return;
        }

        notificationService.createNotification(
                event.getRecipientId(),
                event.getActorId(),
                NotificationType.REPORT,
                content,
                event.getReportId());

        log.info("Created REPORT result notification: actor={}, recipient={}, report={}, status={}",
                event.getActorId(), event.getRecipientId(), event.getReportId(), status);
    }

    private boolean isInvalidEvent(UUID recipientId, UUID actorId) {
        return recipientId == null
                || actorId == null
                || Objects.equals(recipientId, actorId);
    }

    private String buildReasonMessage(String message, String reason) {
        if (reason == null || reason.isBlank()) {
            return message;
        }
        return message + ". Lý do: " + reason;
    }

    @EventListener
    public void handleCommentHiddenByHsd(CommentHiddenByHsdEvent event) {
        if (event == null || event.getRecipientId() == null || event.getCommentId() == null) {
            return;
        }

        notificationService.createNotification(
                event.getRecipientId(),
                null,
                NotificationType.HSD,
                buildReasonMessage(
                        "Bình luận của bạn đã bị thu hồi vì vi phạm tiêu chuẩn cộng đồng",
                        event.getReason()),
                event.getCommentId());

        log.info("Created HSD notification: recipient={}, comment={}",
                event.getRecipientId(), event.getCommentId());
    }

}