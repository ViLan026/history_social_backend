package com.example.history_social_backend.modules.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostFactCheckPreviewResponse {

    UUID postId;
    List<PostFactCheckClaimPreviewResponse> claims;
}