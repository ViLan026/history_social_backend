package com.example.history_social_backend.modules.post.dto.request;

import com.example.history_social_backend.modules.post.domain.PostStatus;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMyPostStatusRequest {

    @NotNull(message = "Trạng thái bài viết không được để trống")
    PostStatus status;
}