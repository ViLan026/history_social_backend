package com.example.history_social_backend.modules.post.dto.response;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AdminPostResponse {

    UUID id;
    String title;
    String contentPreview;

    UUID authorId;
    String authorName;

    PostStatus status;

    Long commentCount;
    Long reactionCount;
    Long bookmarkCount;
    Long reportCount;

    FactCheckSummaryResponse factCheckSummary;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}