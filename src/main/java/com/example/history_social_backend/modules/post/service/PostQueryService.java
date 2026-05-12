package com.example.history_social_backend.modules.post.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.post.dto.response.FeedPostResponse;
import com.example.history_social_backend.modules.post.mapper.PostMapper;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import com.example.history_social_backend.modules.report.dto.response.TargetPreviewResponse;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import com.example.history_social_backend.modules.user.service.UserService;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostQueryService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;

    public boolean existsById(UUID id) {
        return postRepository.existsById(id);
    }

    @Transactional
    public FeedPostResponse getPostById(UUID id) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        UserReactionResponse authorSummary = userService.getUserInfo(post.getAuthorId());

        postRepository.incrementViewCount(id);

        FeedPostResponse response = postMapper.toFeedPostResponse(post);
        response.setAuthor(authorSummary);

        return response;
    }

    @Transactional(readOnly = true)
    public Page<FeedPostResponse> getPublishedPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);

        if (posts.isEmpty()) {
            return posts.map(postMapper::toFeedPostResponse);
        }

        // Lấy danh sách author IDs
        Set<UUID> authorIds = posts.getContent().stream()
                .map(Post::getAuthorId)
                .collect(Collectors.toSet());

        Map<UUID, UserReactionResponse> userMap = userService.getUserReactionInfoMap(authorIds);

        return posts.map(post -> {
            FeedPostResponse response = postMapper.toFeedPostResponse(post);
            UserReactionResponse authorSummary = userMap.get(post.getAuthorId());

            response.setAuthor(authorSummary);

            return response;
        });
    }

    @Transactional(readOnly = true)
    public Page<FeedPostResponse> getPostsByAuthor(UUID authorId, Pageable pageable) {
        Page<Post> posts = postRepository.findByAuthorIdAndStatus(authorId, PostStatus.PUBLISHED, pageable);
        if (posts.isEmpty()) {
            return posts.map(postMapper::toFeedPostResponse);
        }

        UserReactionResponse authorSummary = userService.getUserInfo(authorId);
        return posts.map(post -> {
            FeedPostResponse response = postMapper.toFeedPostResponse(post);
            response.setAuthor(authorSummary);
            return response;
        });
    }

    @Transactional(readOnly = true)
    public Page<FeedPostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(postMapper::toFeedPostResponse);
    }

    @Transactional(readOnly = true)
    public TargetPreviewResponse getPostPreviewForModeration(UUID postId) {
        // Tìm post bao gồm cả bài đã bị xóa mềm (cho admin review)
        Post post = postRepository.findPostIncludingDeleted(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + postId));

        if (post == null) {
            return null; // Post đã bị xóa hoàn toàn hoặc không tồn tại
        }

        // Lấy thông tin author
        String authorName = userService.getUserName(post.getAuthorId());

        // Tạo preview content (giới hạn 500 ký tự)
        String contentPreview = post.getContent();
        if (contentPreview != null && contentPreview.length() > 500) {
            contentPreview = contentPreview.substring(0, 500) + "...";
        }

        return TargetPreviewResponse.builder()
                .id(post.getId())
                .content(contentPreview)
                .authorId(post.getAuthorId())
                .authorName(authorName)
                .isDeleted(post.getDeletedAt() != null)
                .isHiddenByAdmin(false) // TODO: Implement khi có feature hide by admin
                .isHiddenByAuthor(post.getStatus() == PostStatus.DRAFT || post.getStatus() == PostStatus.HIDDEN)
                .build();
    }

}
