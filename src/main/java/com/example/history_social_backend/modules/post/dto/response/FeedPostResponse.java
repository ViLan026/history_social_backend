package com.example.history_social_backend.modules.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;

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
    UserReactionResponse author;

}