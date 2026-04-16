package com.example.history_social_backend.modules.post.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedMessage {
    private UUID postId;
    private UUID authorId;
    private String title;
}