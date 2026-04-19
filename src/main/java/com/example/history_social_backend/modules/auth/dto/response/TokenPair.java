package com.example.history_social_backend.modules.auth.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenPair {
    // Boolean authenticated;
    String refreshToken;
    String accessToken;
}