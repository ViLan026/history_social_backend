package com.example.history_social_backend.common.messaging.producer;

import com.example.history_social_backend.common.messaging.config.RedisMessagingConfig;
import com.example.history_social_backend.common.messaging.model.EventEnvelope;
import com.example.history_social_backend.common.messaging.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer service để publish events vào Redis Streams
 * Thay thế ApplicationEventPublisher
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagingConfig messagingConfig;
    private final ObjectMapper objectMapper;

    /**
     * Publish event SAU KHI transaction commit thành công
     * Tương đương @TransactionalEventListener(phase = AFTER_COMMIT)
     */
    public void publishAfterCommit(EventType eventType, Object payload, String source) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Đăng ký callback để publish sau khi commit
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishEvent(eventType, payload, source);
                }
            });
        } else {
            // Không trong transaction → publish ngay
            publishEvent(eventType, payload, source);
        }
    }

    /**
     * Publish event ngay lập tức (không đợi transaction)
     */
    public void publishEvent(EventType eventType, Object payload, String source) {
        try {
            EventEnvelope envelope = EventEnvelope.create(eventType, payload, source);
            
            // Convert envelope to JSON
            String envelopeJson = objectMapper.writeValueAsString(envelope);
            
            // Tạo stream record
            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("eventType", eventType.name());
            messageBody.put("envelope", envelopeJson);
            
            // Push to Redis Stream
            RecordId recordId = redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                    .in(messagingConfig.getStreamName())
                    .ofMap(messageBody)
            );
            
            log.debug("Published event to Redis Stream: type={}, recordId={}, eventId={}", 
                     eventType, recordId, envelope.getEventId());
                     
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event envelope: type={}", eventType, e);
            throw new RuntimeException("Failed to publish event", e);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis: type={}", eventType, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Publish event ngay lập tức với source mặc định
     */
    public void publishEvent(EventType eventType, Object payload) {
        publishEvent(eventType, payload, "unknown");
    }
}