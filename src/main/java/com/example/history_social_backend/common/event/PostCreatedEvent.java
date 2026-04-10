// common/event/PostCreatedEvent.java
package com.example.history_social_backend.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event được publish khi một bài viết mới được tạo thành công.
 * Các module khác (notification, search-index, audit-log) lắng nghe event này.
 */
@Getter
public class PostCreatedEvent extends ApplicationEvent {

    private final UUID postId;
    private final UUID authorId;
    private final String title;

    public PostCreatedEvent(Object source, UUID postId, UUID authorId, String title) {
        super(source);
        this.postId   = postId;
        this.authorId = authorId;
        this.title    = title;
    }
}