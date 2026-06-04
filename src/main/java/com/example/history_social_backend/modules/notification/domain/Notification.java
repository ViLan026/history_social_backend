package com.example.history_social_backend.modules.notification.domain;

import com.example.history_social_backend.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue
    UUID id;

    @Column(name = "receiver_id", nullable = false)
    UUID receiverId;

    @Column(name = "actor_id")
    UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    NotificationType type;

    @Column(nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String message;

    @Column(name = "target_id")
    UUID targetId;

    @Column(name = "target_type", length = 50)
    String targetType;

    @Column(name = "is_read", nullable = false)
    boolean read;
}