package com.example.history_social_backend.modules.report.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TargetPreviewResponse {

    UUID id;
    String title;
    String content;

    UUID authorId;
    String authorName;

    String targetStatus;
    Long reportCount;

    Boolean isDeleted;
    Boolean isHiddenByAdmin;
    Boolean isHiddenByAuthor;

    List<PostFactCheckClaimResponse> factCheckClaims;
    HateSpeechResultResponse hateSpeechResult;
}