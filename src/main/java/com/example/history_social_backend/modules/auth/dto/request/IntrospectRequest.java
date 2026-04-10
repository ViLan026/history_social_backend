package com.example.history_social_backend.modules.auth.dto.request;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequest {
    private String token;
}
