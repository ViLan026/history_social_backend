package com.example.history_social_backend.modules.feed.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.post.dto.response.PostMediaResponse;
import com.example.history_social_backend.modules.post.dto.response.PostSourceResponse;
import com.example.history_social_backend.modules.post.dto.response.TagResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedPostResponse {

    UUID postId;
    String content;
    String title;
    Long viewCount;
    Long reactionCount;
    Long commentCount;
    PostStatus status;
    List<PostMediaResponse> mediaList;
    Set<PostSourceResponse> sources;
    Set<TagResponse> tags;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    AuthorSummary author;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AuthorSummary {
        UUID userId;
        String displayName;
        String avatarUrl;
    }
}