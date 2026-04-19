package com.example.history_social_backend.modules.notification.domain;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;
    
    @Column(name = "recipient_id", nullable = false)
    UUID recipientId;

    @Column(name = "actor_id")
    UUID actorId;

    @Column(name = "reference_id")
    UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationType type;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(name = "is_read", nullable = false)
    boolean isRead = false;

    public void markAsRead() {
        this.isRead = true;
    }
}