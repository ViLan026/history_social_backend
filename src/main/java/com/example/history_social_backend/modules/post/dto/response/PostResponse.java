package com.example.history_social_backend.modules.post.dto.response;

import com.example.history_social_backend.modules.post.domain.PostStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Value
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    UUID id;
    String title;
    String content;
    UUID authorId;
    PostStatus status;
    long viewCount;
    List<PostMediaResponse> mediaList;
    Set<PostSourceResponse> sources;
    Set<TagResponse> tags;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}