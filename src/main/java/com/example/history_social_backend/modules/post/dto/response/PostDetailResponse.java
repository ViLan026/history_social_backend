package com.example.history_social_backend.modules.post.dto.response;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDetailResponse {

    UUID postId;
    String title;
    String content;

    Long reactionCount;
    Long commentCount;
    Long bookmarkCount;

    PostStatus status;

    List<PostMediaResponse> mediaList;
    Set<PostSourceResponse> sources;
    Set<TagResponse> tags;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    UserReactionResponse author;

    FactCheckSummaryResponse factCheckSummary;
    List<PostFactCheckClaimResponse> factCheckClaims;
}