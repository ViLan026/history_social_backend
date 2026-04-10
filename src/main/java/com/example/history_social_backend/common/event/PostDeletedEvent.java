package com.example.history_social_backend.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

//  Event publish SAU KHI xóa bài viết thành công (bao gồm media trên Cloudinary đã được dọn sạch).
@Getter
public class PostDeletedEvent extends ApplicationEvent {

    private final UUID postId;
    private final UUID deletedBy;

    public PostDeletedEvent(Object source, UUID postId, UUID deletedBy) {
        super(source);
        this.postId    = postId;
        this.deletedBy = deletedBy;
    }
}