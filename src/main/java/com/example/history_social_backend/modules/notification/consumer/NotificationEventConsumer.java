package com.example.history_social_backend.modules.notification.consumer;

import com.example.history_social_backend.common.messaging.config.RedisMessagingConfig;
import com.example.history_social_backend.common.messaging.model.EventEnvelope;
import com.example.history_social_backend.modules.comment.message.CommentCreatedMessage;
import com.example.history_social_backend.modules.notification.domain.NotificationType;
import com.example.history_social_backend.modules.notification.service.NotificationService;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import com.example.history_social_backend.modules.reaction.message.ReactionAddedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Consumer xử lý events từ Redis Streams và tạo notifications
 * Thay thế NotificationEventListener
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagingConfig messagingConfig;
    private final ObjectMapper objectMapper;
    private final StreamMessageListenerContainer<String, MapRecord<String, Object, Object>> listenerContainer;

    @Value("${messaging.redis.stream.batch-size:10}")
    private int batchSize;

    @Value("${messaging.redis.stream.max-retry-count:5}")
    private int maxRetryCount;

    @Value("${messaging.redis.stream.claim-min-idle-time:300000}")
    private long claimMinIdleTime;

    @Value("${messaging.redis.stream.pending-messages-check-interval:60000}")
    private long pendingCheckInterval;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void startConsuming() {
        log.info("Starting NotificationEventConsumer...");

        // Subscribe to stream với consumer group
        Consumer consumer = Consumer.from(
                messagingConfig.getConsumerGroup(),
                messagingConfig.getConsumerName());

        StreamOffset<String> streamOffset = StreamOffset.create(
                messagingConfig.getStreamName(),
                ReadOffset.lastConsumed());

        // Register message listener
        listenerContainer.receive(
                consumer,
                streamOffset,
                this::handleMessage);

        listenerContainer.start();

        // Start scheduler để xử lý pending messages
        startPendingMessageProcessor();

        log.info("NotificationEventConsumer started successfully");
    }

    @PreDestroy
    public void stopConsuming() {
        log.info("Stopping NotificationEventConsumer...");
        if (listenerContainer != null) {
            listenerContainer.stop();
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
        log.info("NotificationEventConsumer stopped");
    }

    // Xử lý message từ stream
    private void handleMessage(MapRecord<String, Object, Object> record) {
        try {
            String envelopeJson = (String) record.getValue().get("envelope");

            EventEnvelope envelope = objectMapper.readValue(envelopeJson, EventEnvelope.class);

            log.debug("Processing event: type={}, eventId={}",
                    envelope.getEventType(),
                    envelope.getEventId());

            switch (envelope.getEventType()) {
                case REACTION_ADDED -> handleReactionAdded(envelope);
                case COMMENT_CREATED -> handleCommentCreated(envelope);
                // case FOLLOW_CREATED -> handleFollowCreated(envelope);

                case POST_CREATED, POST_UPDATED, POST_DELETED -> {
                    log.debug("Received {} event, no notification action needed",
                            envelope.getEventType());
                }

                default -> log.warn("Unknown event type: {}",
                        envelope.getEventType());
            }

            redisTemplate.opsForStream().acknowledge(
                    messagingConfig.getStreamName(),
                    messagingConfig.getConsumerGroup(),
                    record.getId());

            log.debug("ACK success: {}", envelope.getEventId());

        } catch (Exception e) {
            log.error("Error processing recordId={}", record.getId(), e);
            // no ACK => pending retry
        }
    }

    private void handleReactionAdded(EventEnvelope envelope) {
        try {
            ReactionAddedMessage message = objectMapper.convertValue(
                    envelope.getPayload(),
                    ReactionAddedMessage.class);

            Post post = postRepository.findById(message.getPostId()).orElse(null);
            if (post == null) {
                log.warn("Post not found for reaction: postId={}", message.getPostId());
                return;
            }

            UUID recipientId = post.getAuthorId();
            UUID actorId = message.getUserId();

            // Check self-interaction
            if (actorId.equals(recipientId)) {
                log.debug("Self-interaction detected, skipping notification");
                return;
            }

            notificationService.createInteractionNotification(
                    actorId,
                    recipientId,
                    message.getPostId(),
                    NotificationType.LIKE,
                    null);

            log.debug("Created LIKE notification: actor={}, recipient={}", actorId, recipientId);

        } catch (Exception e) {
            log.error("Error handling ReactionAdded event", e);
            throw e;
        }
    }

    private void handleCommentCreated(EventEnvelope envelope) {
        try {
            CommentCreatedMessage message = objectMapper.convertValue(
                    envelope.getPayload(),
                    CommentCreatedMessage.class);

            Post post = postRepository.findById(message.getPostId()).orElse(null);
            if (post == null) {
                log.warn("Post not found for comment: postId={}", message.getPostId());
                return;
            }

            UUID recipientId = post.getAuthorId();
            UUID actorId = message.getAuthorId();

            // Check self-interaction
            if (actorId.equals(recipientId)) {
                log.debug("Self-interaction detected, skipping notification");
                return;
            }

            notificationService.createInteractionNotification(
                    actorId,
                    recipientId,
                    message.getPostId(),
                    NotificationType.COMMENT,
                    null);

            log.debug("Created COMMENT notification: actor={}, recipient={}", actorId, recipientId);

        } catch (Exception e) {
            log.error("Error handling CommentCreated event", e);
            throw e;
        }
    }

    // private void handleFollowCreated(EventEnvelope envelope) {
    // try {
    // FollowCreatedMessage message = objectMapper.convertValue(
    // envelope.getPayload(),
    // FollowCreatedMessage.class);

    // UUID actorId = message.getFollowerId();
    // UUID recipientId = message.getFollowedId();

    // // Check self-interaction
    // if (actorId.equals(recipientId)) {
    // log.debug("Self-interaction detected, skipping notification");
    // return;
    // }

    // notificationService.createInteractionNotification(
    // actorId,
    // recipientId,
    // null,
    // NotificationType.FOLLOW,
    // null);

    // log.debug("Created FOLLOW notification: actor={}, recipient={}", actorId,
    // recipientId);

    // } catch (Exception e) {
    // log.error("Error handling FollowCreated event", e);
    // throw e;
    // }
    // }

    // Background task để xử lý pending messages (retry logic)
    private void startPendingMessageProcessor() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::processPendingMessages,
                pendingCheckInterval,
                pendingCheckInterval,
                TimeUnit.MILLISECONDS);
        log.info("Pending message processor started with interval: {}ms", pendingCheckInterval);
    }

    private void processPendingMessages() {
        try {
            // Lấy pending messages
            PendingMessagesSummary summary = redisTemplate.opsForStream().pending(
                    messagingConfig.getStreamName(),
                    messagingConfig.getConsumerGroup());

            if (summary == null || summary.getTotalPendingMessages() == 0) {
                return;
            }

            log.info("Found {} pending messages", summary.getTotalPendingMessages());

            // Lấy chi tiết pending messages
            PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                    messagingConfig.getStreamName(),
                    Consumer.from(messagingConfig.getConsumerGroup(), messagingConfig.getConsumerName()),
                    Range.unbounded(),
                    batchSize);

            for (PendingMessage pm : pendingMessages) {
                // Claim message nếu idle quá lâu
                if (pm.getElapsedTimeSinceLastDelivery().toMillis() > claimMinIdleTime) {

                    // Check retry count
                    if (pm.getTotalDeliveryCount() >= maxRetryCount) {
                        log.error("Message exceeded max retry count, moving to DLQ: messageId={}, retryCount={}",
                                pm.getId(), pm.getTotalDeliveryCount());
                        // Move to Dead Letter Queue
                        // Tạm thời ACK để không block stream
                        redisTemplate.opsForStream().acknowledge(
                                messagingConfig.getStreamName(),
                                messagingConfig.getConsumerGroup(),
                                pm.getId());
                        continue;
                    }

                    // Claim và retry
                    List<MapRecord<String, Object, Object>> claimedRecords = redisTemplate.opsForStream().claim(
                            messagingConfig.getStreamName(),
                            messagingConfig.getConsumerGroup(),
                            messagingConfig.getConsumerName(),
                            Duration.ofMillis(claimMinIdleTime),
                            pm.getId());

                    // Process claimed messages
                    if (claimedRecords != null) {
                        claimedRecords.forEach(this::handleMessage);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error processing pending messages", e);
        }
    }
}