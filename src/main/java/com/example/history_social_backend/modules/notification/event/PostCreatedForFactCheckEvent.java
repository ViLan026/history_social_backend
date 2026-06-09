package com.example.history_social_backend.modules.notification.event;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PostCreatedForFactCheckEvent {

    private UUID postId;
    private UUID authorId;
}