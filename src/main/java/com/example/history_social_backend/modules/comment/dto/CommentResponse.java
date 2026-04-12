package com.example.history_social_backend.modules.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    UUID id;
    UUID postId;
    UUID authorId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}