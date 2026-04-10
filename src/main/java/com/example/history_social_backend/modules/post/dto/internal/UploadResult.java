package com.example.history_social_backend.modules.post.dto.internal;


import lombok.Builder;
import lombok.Value;

/**
 * Value object chứa kết quả upload từ Cloudinary.
 * Dùng nội bộ giữa CloudinaryService và PostService.
 */
@Value
@Builder
public class UploadResult {
    String mediaUrl;    // https://res.cloudinary.com/...
    String publicId;    // history_social/posts/abc123
    String format;      // jpg, mp4, pdf ...
    long   bytes;
    String resourceType; // image, video, raw ...
}