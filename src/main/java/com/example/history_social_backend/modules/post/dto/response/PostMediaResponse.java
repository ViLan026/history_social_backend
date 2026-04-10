package com.example.history_social_backend.modules.post.dto.response;

import com.example.history_social_backend.modules.post.domain.MediaType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;
import lombok.experimental.FieldDefaults;

@Value
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMediaResponse {
    UUID id;
    String mediaUrl;
    String publicId;
    MediaType mediaType;
    int displayOrder;
}