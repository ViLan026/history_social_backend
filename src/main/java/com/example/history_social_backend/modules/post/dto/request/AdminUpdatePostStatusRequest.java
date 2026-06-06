package com.example.history_social_backend.modules.post.dto.request;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdatePostStatusRequest {

    @NotNull(message = "Post status is required")
    private PostStatus status;
}