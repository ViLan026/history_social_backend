package com.example.history_social_backend.modules.post.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Value
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSourceResponse {
    UUID id;
    String title;
    String url;
    String author;
    Integer publishedYear;
}