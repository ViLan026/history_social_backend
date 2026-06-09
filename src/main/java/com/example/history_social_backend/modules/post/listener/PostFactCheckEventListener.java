package com.example.history_social_backend.modules.post.listener;

import com.example.history_social_backend.modules.notification.event.PostCreatedForFactCheckEvent;
import com.example.history_social_backend.modules.notification.event.PostFactCheckCompletedEvent;
import com.example.history_social_backend.modules.post.service.PostFactCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostFactCheckEventListener {

    private final PostFactCheckService postFactCheckService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void handlePostCreatedForFactCheck(PostCreatedForFactCheckEvent event) {
        if (event == null || event.getPostId() == null || event.getAuthorId() == null) {
            return;
        }

        try {
            postFactCheckService.recheckPostById(event.getPostId());

            eventPublisher.publishEvent(
                    PostFactCheckCompletedEvent.builder()
                            .postId(event.getPostId())
                            .recipientId(event.getAuthorId())
                            .actorId(null)
                            .build()
            );

            log.info("Async fact-check completed: postId={}", event.getPostId());
        } catch (Exception e) {
            log.error("Async fact-check failed: postId={}", event.getPostId(), e);
        }
    }
}