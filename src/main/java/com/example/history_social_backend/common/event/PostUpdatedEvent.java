// common/event/PostUpdatedEvent.java
package com.example.history_social_backend.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event publish khi bài viết được cập nhật (nội dung, trạng thái, media).
 */
@Getter
public class PostUpdatedEvent extends ApplicationEvent {

    private final UUID   postId;
    private final UUID   updatedBy;
    private final String changeDescription;   // Mô tả ngắn gọn thay đổi

    public PostUpdatedEvent(Object source, UUID postId, UUID updatedBy, String changeDescription) {
        super(source);
        this.postId            = postId;
        this.updatedBy         = updatedBy;
        this.changeDescription = changeDescription;
    }
}