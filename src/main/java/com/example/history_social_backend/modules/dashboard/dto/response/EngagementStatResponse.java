package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementStatResponse {

    private LocalDate date;
    private long comments;
    private long reactions;
    private long bookmarks;
    private long follows;
}