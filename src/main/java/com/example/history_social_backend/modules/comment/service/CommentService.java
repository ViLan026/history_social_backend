package com.example.history_social_backend.modules.comment.service;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.ai.dto.response.AiHateSpeechResponse;
import com.example.history_social_backend.modules.ai.service.AiModerationService;
import com.example.history_social_backend.modules.comment.domain.Comment;
import com.example.history_social_backend.modules.comment.dto.CommentRequest;
import com.example.history_social_backend.modules.comment.dto.CommentResponse;
import com.example.history_social_backend.modules.comment.mapper.CommentMapper;
import com.example.history_social_backend.modules.comment.repository.CommentRepository;
import com.example.history_social_backend.modules.post.dto.response.FeedPostResponse;
import com.example.history_social_backend.modules.post.service.PostQueryService;
import com.example.history_social_backend.modules.report.dto.response.TargetPreviewResponse;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import com.example.history_social_backend.modules.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.example.history_social_backend.modules.notification.event.CommentCreatedEvent;
import com.example.history_social_backend.modules.notification.event.CommentRepliedEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostQueryService postQueryService;
    private final CommentRepository commentRepository;
    private final UserQueryService userQueryService;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final AiModerationService aiModerationService;
    private final CommentHateSpeechService commentHateSpeechService;

    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        UUID authorId = SecurityUtils.getCurrentUserId();
        UUID postId = request.getPostId();

        postQueryService.increaseCommentCount(postId);

        Comment parentComment = null;

        if (request.getParentId() != null) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());

        comment.validateContent();

        Comment savedComment = commentRepository.save(comment);

        AiHateSpeechResponse hsdResult = aiModerationService.detectCommentHateSpeech(request.getContent());

        if (commentHateSpeechService.isHateSpeech(hsdResult)) {
            throw new AppException(ErrorCode.COMMENT_CONTAINS_HATE_SPEECH);
        }

        FeedPostResponse post = postQueryService.getPostById(postId);
        UUID postOwnerId = post.getAuthor().getUserId();

        UserReactionResponse actorProfile = userQueryService.getUserInfo(authorId);
        String senderName = actorProfile.getDisplayName();

        if (parentComment == null) {
            publishCommentNotification(
                    postId,
                    savedComment.getId(),
                    authorId,
                    postOwnerId,
                    senderName);
        } else {
            UUID parentCommentAuthorId = parentComment.getAuthorId();

            publishReplyNotification(
                    postId,
                    parentComment.getId(),
                    savedComment.getId(),
                    authorId,
                    parentCommentAuthorId,
                    senderName);

            publishCommentNotificationToPostOwnerAfterReply(
                    postId,
                    savedComment.getId(),
                    authorId,
                    postOwnerId,
                    parentCommentAuthorId,
                    senderName);
        }

        return commentMapper.toResponse(savedComment);
    }

    private void publishCommentNotification(
            UUID postId,
            UUID commentId,
            UUID actorId,
            UUID postOwnerId,
            String senderName) {
        if (postOwnerId == null || postOwnerId.equals(actorId)) {
            return;
        }

        applicationEventPublisher.publishEvent(
                CommentCreatedEvent.builder()
                        .postId(postId)
                        .commentId(commentId)
                        .actorId(actorId)
                        .recipientId(postOwnerId)
                        .senderName(senderName)
                        .build());
    }

    private void publishReplyNotification(
            UUID postId,
            UUID parentCommentId,
            UUID replyCommentId,
            UUID actorId,
            UUID parentCommentAuthorId,
            String senderName) {
        if (parentCommentAuthorId == null || parentCommentAuthorId.equals(actorId)) {
            return;
        }

        applicationEventPublisher.publishEvent(
                CommentRepliedEvent.builder()
                        .postId(postId)
                        .parentCommentId(parentCommentId)
                        .replyCommentId(replyCommentId)
                        .actorId(actorId)
                        .recipientId(parentCommentAuthorId)
                        .senderName(senderName)
                        .build());
    }

    private void publishCommentNotificationToPostOwnerAfterReply(
            UUID postId,
            UUID replyCommentId,
            UUID actorId,
            UUID postOwnerId,
            UUID parentCommentAuthorId,
            String senderName) {
        if (postOwnerId == null) {
            return;
        }

        if (postOwnerId.equals(actorId)) {
            return;
        }

        if (postOwnerId.equals(parentCommentAuthorId)) {
            return;
        }

        applicationEventPublisher.publishEvent(
                CommentCreatedEvent.builder()
                        .postId(postId)
                        .commentId(replyCommentId)
                        .actorId(actorId)
                        .recipientId(postOwnerId)
                        .senderName(senderName)
                        .build());
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsByPostId(UUID postId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Comment> commentPage = commentRepository.findByPostIdAndNotDeleted(postId, pageable);

        // Lấy toàn bộ authorId trong page hiện tại
        Set<UUID> authorIds = commentPage.getContent()
                .stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());

        // Query 1 lần
        Map<UUID, UserReactionResponse> userMap = userQueryService.getUserReactionInfoMap(authorIds);

        Page<CommentResponse> responsePage = commentPage.map(comment -> {

            CommentResponse response = commentMapper.toResponse(comment);

            UserReactionResponse userInfo = userMap.get(comment.getAuthorId());

            if (userInfo != null) {
                response.setAuthorName(userInfo.getDisplayName());
                response.setAuthorAvatarUrl(userInfo.getAvatarUrl());
            }

            return response;
        });

        return PageResponse.from(responsePage);
    }

    @Transactional
    public String deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getDeletedAt() != null) {
            throw new AppException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        UUID authorId = SecurityUtils.getCurrentUserId();

        boolean isAuthor = comment.getAuthorId().equals(authorId);
        boolean isAdmin = isCurrentUserAdmin();

        if (!isAuthor && !isAdmin) {
            throw new AppException(ErrorCode.DELETE_COMMENT_FORBIDDEN);
        }

        comment.markAsDeleted();
        commentRepository.save(comment);

        return "success";
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean existsById(UUID id) {
        return commentRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public UUID getPostIdByCommentId(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND))
                .getPostId();
    }

    @Transactional(readOnly = true)
    public TargetPreviewResponse getCommentPreviewForModeration(UUID commentId) {
        return commentRepository.findByIdIncludingDeleted(commentId)
                .map(projection -> {
                    String authorName = userQueryService.getUserName(projection.getAuthorId());

                    // Tạo preview content (giới hạn 300 ký tự cho comment)
                    String content = projection.getContent();
                    String contentPreview = content;
                    if (content != null && content.length() > 300) {
                        contentPreview = content.substring(0, 300) + "...";
                    }

                    return TargetPreviewResponse.builder()
                            .id(projection.getId())
                            .content(contentPreview)
                            .authorId(projection.getAuthorId())
                            .authorName(authorName)
                            .isDeleted(projection.getDeletedAt() != null)
                            .isHiddenByAdmin(false)
                            .isHiddenByAuthor(false)
                            .build();
                })
                .orElse(null);
    }

    @Transactional
    public void deleteCommentByAdmin(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        comment.markAsDeleted();
        commentRepository.save(comment);
    }
}
