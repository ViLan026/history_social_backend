package com.example.history_social_backend.common.event;

import java.util.UUID;


public record FollowCreatedEvent(
        UUID followerId,   // actor
        UUID followedId    // recipient
) {
}