package com.example.history_social_backend.modules.ai.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiClaimResponse {
    private String claim;
    private String label;
    private String explanation;
    private List<Object> evidence;
}