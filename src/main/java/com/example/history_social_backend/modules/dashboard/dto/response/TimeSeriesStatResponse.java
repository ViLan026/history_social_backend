package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesStatResponse {

    private LocalDate date;
    private long count;
}