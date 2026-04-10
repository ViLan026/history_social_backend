package com.example.history_social_backend.modules.notification.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemNotificationRequest {
    private UUID recipientId;   // null = gửi cho TẤT CẢ user
    private String content;
}