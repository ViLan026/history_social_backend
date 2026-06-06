package com.example.history_social_backend.modules.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class FactCheckSummaryResponse {

    long supportedCount;
    long refutedCount;
    long notEnoughEvidenceCount;
}