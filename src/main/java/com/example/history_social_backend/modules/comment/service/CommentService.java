package com.example.history_social_backend.modules.comment.service;

import com.example.history_social_backend.common.event.CommentCreatedEvent;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.comment.domain.Comment;
import com.example.history_social_backend.modules.comment.dto.CommentRequest;
import com.example.history_social_backend.modules.comment.dto.CommentResponse;
import com.example.history_social_backend.modules.comment.mapper.CommentMapper;
import com.example.history_social_backend.modules.comment.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public ApiResponse<CommentResponse> createComment(CommentRequest request) {

        UUID authorId = SecurityUtils.getCurrentUserId();

        Comment comment = new Comment();
        comment.setPost(request.getPostId());
        comment.setAuthor(authorId);
        comment.setContent(request.getContent());

        comment.validateContent();

        Comment savedComment = commentRepository.save(comment);

        // Publish event sau khi lưu thành công
        eventPublisher.publishEvent(new CommentCreatedEvent(
                savedComment.getId(),
                request.getPostId(),
                authorId));

        CommentResponse response = commentMapper.toResponse(savedComment);
        return ApiResponse.success("Comment created successfully", response);
    }

    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<CommentResponse>> getCommentsByPostId(UUID postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByPostIdAndNotDeleted(postId, pageable);

        Page<CommentResponse> responsePage = commentPage.map(commentMapper::toResponse);
        PageResponse<CommentResponse> pageResponse = PageResponse.from(responsePage);

        return ApiResponse.success(pageResponse);
    }

    @Transactional
    public ApiResponse<Void> deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getDeletedAt() != null) {
            throw new AppException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        UUID authorId = SecurityUtils.getCurrentUserId();

        boolean isAuthor = comment.getAuthor().equals(authorId);
        boolean isAdmin = isCurrentUserAdmin();

        if (!isAuthor && !isAdmin) {
            throw new AppException(ErrorCode.DELETE_COMMENT_FORBIDDEN);
        }

        comment.markAsDeleted();
        commentRepository.save(comment);

        return ApiResponse.success("Comment deleted successfully", null);
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}