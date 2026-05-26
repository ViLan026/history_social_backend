package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopReportedPostResponse {

    private UUID postId;
    private String title;
    private UUID authorId;
    private String status;
    private long reportCount;
    private Double qualityScore;
    private LocalDateTime createdAt;
}