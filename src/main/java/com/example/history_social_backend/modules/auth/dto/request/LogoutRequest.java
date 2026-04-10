package com.example.history_social_backend.modules.auth.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequest {
    private String refreshToken;
}