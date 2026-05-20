package com.example.history_social_backend.modules.follow.dto.response;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowResponse {

    UUID userId;

    String username;

    String displayName;

    String avatarUrl;
}