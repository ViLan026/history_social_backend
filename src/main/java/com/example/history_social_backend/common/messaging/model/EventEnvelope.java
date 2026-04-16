package com.example.history_social_backend.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Envelope wrapper cho tất cả domain events trong Redis Streams
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope {
    
    private String eventId;           // UUID của event
    private EventType eventType;      // Loại event
    private Instant occurredAt;       // Thời điểm xảy ra
    
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Object payload;           // Actual event data
    
    private int retryCount;           // Số lần retry
    private String traceId;           // Distributed tracing (optional)
    private String source;            // Service/module phát sinh event
    
    public static EventEnvelope create(EventType type, Object payload, String source) {
        return EventEnvelope.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(type)
                .payload(payload)
                .occurredAt(Instant.now())
                .retryCount(0)
                .source(source)
                .build();
    }
    
    public void incrementRetry() {
        this.retryCount++;
    }
}