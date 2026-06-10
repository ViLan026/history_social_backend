// modules/post/dto/response/UpdatePostStatusResponse.java

package com.example.history_social_backend.modules.post.dto.response;

import java.util.UUID;

import com.example.history_social_backend.modules.post.domain.PostStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePostStatusResponse {

    UUID postId;
    PostStatus status;
}