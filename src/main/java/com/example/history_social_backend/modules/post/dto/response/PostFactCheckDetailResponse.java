package com.example.history_social_backend.modules.post.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostFactCheckDetailResponse {
    UUID postId;
    FactCheckSummaryResponse summary;
    List<PostFactCheckClaimResponse> claims;
}
