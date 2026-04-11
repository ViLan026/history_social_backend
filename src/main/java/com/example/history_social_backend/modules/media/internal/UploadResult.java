package com.example.history_social_backend.modules.media.internal;


import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class UploadResult {
    String mediaUrl;    // https://res.cloudinary.com/...
    String publicId;    // history_social/posts/abc123
    String format;      // jpg, mp4, pdf ...
    long   bytes;
    String resourceType; // image, video, ...
}