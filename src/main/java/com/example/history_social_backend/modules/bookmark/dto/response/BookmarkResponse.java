package com.example.history_social_backend.modules.bookmark.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookmarkResponse {

    UUID bookmarkId;
    LocalDateTime bookmarkedAt;

    // Post information
    PostInfo post;

    // Nested DTO for post information
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PostInfo {
        UUID id;
        String title;
        String content;
        String summary;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        // Author information
        AuthorInfo author;

        // Post statistics
        long viewCount;
        long likeCount;
        long commentCount;
    }

    // Nested DTO for post author information
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AuthorInfo {
        UUID id;
        String username;
        String displayName;
        String avatarUrl;
    }
}