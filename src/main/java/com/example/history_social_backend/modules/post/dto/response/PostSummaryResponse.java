package com.example.history_social_backend.modules.post.dto.response;

import com.example.history_social_backend.modules.post.domain.PostStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSummaryResponse {
    UUID id;
    String title;
    UUID authorId;
    PostStatus status;
    long viewCount;
    String thumbnailUrl; // URL ảnh đại diện (media đầu tiên)
    Set<TagResponse> tags;
    LocalDateTime createdAt;
}