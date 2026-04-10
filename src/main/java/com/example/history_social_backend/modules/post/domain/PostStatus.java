package com.example.history_social_backend.modules.post.domain;

public enum PostStatus {
    DRAFT,        // Nháp
    PUBLISHED,
    HIDDEN,        // người dùng ẩn bài viết
    FLAGGED,      // bị báo cáo nên gắn cờ để xem xét
    REJECTED,    // hệ thống check thấy vi phạm
}
 