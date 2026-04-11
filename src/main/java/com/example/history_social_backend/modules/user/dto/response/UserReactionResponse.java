package com.example.history_social_backend.modules.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserReactionResponse {
    UUID id;
    String displayName;
    String avatarUrl;
}