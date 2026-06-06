package com.example.history_social_backend.modules.notification.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportCreatedEvent {

    UUID reportId;
    UUID actorId;
    UUID adminId;
    String senderName;
}