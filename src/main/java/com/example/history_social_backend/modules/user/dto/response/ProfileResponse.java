package com.example.history_social_backend.modules.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    UUID userId;
    String displayName;
    String username;
    String avatarUrl;
    String bio;
}