package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopTagResponse {

    private UUID tagId;
    private String name;
    private long usageCount;
}