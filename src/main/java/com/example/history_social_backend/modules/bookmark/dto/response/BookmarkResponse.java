package com.example.history_social_backend.modules.bookmark.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.history_social_backend.modules.post.dto.response.FeedPostResponse;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookmarkResponse {

    UUID bookmarkId;
    LocalDateTime bookmarkedAt;

    FeedPostResponse post;
}