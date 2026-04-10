package com.example.history_social_backend.modules.post.service;

import com.example.history_social_backend.common.event.PostCreatedEvent;
import com.example.history_social_backend.common.event.PostDeletedEvent;
import com.example.history_social_backend.common.event.PostUpdatedEvent;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.media.service.CloudinaryService;
import com.example.history_social_backend.modules.post.domain.*;
import com.example.history_social_backend.modules.post.dto.internal.UploadResult;
import com.example.history_social_backend.modules.post.dto.request.PostCreationRequest;
import com.example.history_social_backend.modules.post.dto.request.PostUpdateRequest;
import com.example.history_social_backend.modules.post.dto.response.PostResponse;
import com.example.history_social_backend.modules.post.dto.response.PostSummaryResponse;
import com.example.history_social_backend.modules.post.mapper.PostMapper;
import com.example.history_social_backend.modules.post.repository.PostMediaRepository;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import com.example.history_social_backend.modules.post.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final TagRepository tagRepository;
    private final CloudinaryService cloudinaryService;
    private final PostMapper postMapper;
    private final ApplicationEventPublisher eventPublisher;

    public boolean existsById(UUID id) {
        return postRepository.existsById(id);
    }

    public PostResponse createPost(PostCreationRequest request,
            List<MultipartFile> files,
            UUID authorId) {
        // Upload files lên Cloudinary TRƯỚC khi mở DB transaction
        List<UploadResult> uploadResults = uploadFilesWithRollback(files);

        try {
            // Lưu vào DB trong transaction
            Post savedPost = savePostWithinTransaction(request, uploadResults, authorId);

            // Publish event (sau khi transaction commit)
            eventPublisher.publishEvent(new PostCreatedEvent(
                    this, savedPost.getId(), authorId, savedPost.getTitle()));

            return postMapper.toPostResponse(savedPost);

        } catch (Exception e) {
            // xóa các file đã upload trên Cloudinary nếu DB lỗi
            log.warn("DB save failed after Cloudinary upload — compensating by deleting {} assets",
                    uploadResults.size());

            uploadResults.forEach(r -> {
                try {
                    cloudinaryService.deleteFile(r.getPublicId(), r.getResourceType());
                } catch (Exception ce) {
                    log.error("Compensation delete failed: {}", r.getPublicId(), ce);
                }
            });
            throw e;
        }
    }

    // Lưu bài viết trong transaction, trả về entity đã lưu (có ID) để publish event
    // sau commit.
    @Transactional
    protected Post savePostWithinTransaction(PostCreationRequest request,
            List<UploadResult> uploadResults,
            UUID authorId) {
        // Build Post entity
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(authorId)
                .status(request.getStatus())
                .build();

        // Gắn media từ kết quả upload
        for (int i = 0; i < uploadResults.size(); i++) {
            UploadResult ur = uploadResults.get(i);
            PostMedia media = PostMedia.builder()
                    .mediaUrl(ur.getMediaUrl())
                    .publicId(ur.getPublicId())
                    .resourceType(ur.getResourceType())
                    .mediaType(cloudinaryService.resolveMediaType(ur.getFormat()))
                    .displayOrder(i)
                    .build();
            post.addMedia(media);
        }

        // Gắn sources
        if (!CollectionUtils.isEmpty(request.getSources())) {
            request.getSources().stream()
                    .map(postMapper::toSourceEntity)
                    .forEach(post::addSource);
        }

        // Resolve tags (tìm tag cũ hoặc tạo mới nếu chưa tồn tại)
        if (!CollectionUtils.isEmpty(request.getTagNames())) {
            Set<Tag> tags = resolveOrCreateTags(request.getTagNames());
            tags.forEach(tag -> {
                PostTag postTag = PostTag.builder()
                        .id(new PostTagId(post.getId(), tag.getId()))
                        .post(post)
                        .tag(tag)
                        .build();
                post.getPostTags().add(postTag);
            });
        }

        return postRepository.save(post);
    }


    // READ

    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        postRepository.incrementViewCount(id);
        return postMapper.toPostResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatus(PostStatus.PUBLISHED, pageable)
                .map(postMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostsByAuthor(UUID authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable)
                .map(postMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(postMapper::toSummaryResponse);
    }

    // UPDATE
    // Cập nhật bài viết — hỗ trợ thêm file mới và xóa media cũ
    public PostResponse updatePost(UUID postId,
            PostUpdateRequest request,
            List<MultipartFile> newFiles,
            UUID currentUserId) {
        Post post = postRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Kiểm tra quyền (chỉ tác giả mới được sửa)
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }

        // Upload file mới trước transaction
        List<UploadResult> newUploads = uploadFilesWithRollback(newFiles);

        // Xóa media cũ trên Cloudinary nếu được yêu cầu
        deleteRequestedMedia(request, post);

        // Lưu vào DB
        Post updated = applyUpdateWithinTransaction(post, request, newUploads, currentUserId);

        eventPublisher.publishEvent(new PostUpdatedEvent(
                this, updated.getId(), currentUserId, "Post content updated"));

        return postMapper.toPostResponse(updated);
    }

    @Transactional
    protected Post applyUpdateWithinTransaction(Post post,
            PostUpdateRequest request,
            List<UploadResult> newUploads,
            UUID updatedBy) {
        if (request.getTitle() != null)
            post.setTitle(request.getTitle());
        if (request.getContent() != null)
            post.setContent(request.getContent());
        // if (request.getSummary() != null) post.setSummary(request.getSummary());
        if (request.getStatus() != null)
            post.setStatus(request.getStatus());

        // Thêm media mới
        int nextOrder = post.getMediaList().size();
        for (int i = 0; i < newUploads.size(); i++) {
            UploadResult ur = newUploads.get(i);
            PostMedia media = PostMedia.builder()
                    .mediaUrl(ur.getMediaUrl())
                    .publicId(ur.getPublicId())
                    .resourceType(ur.getResourceType())
                    .mediaType(cloudinaryService.resolveMediaType(ur.getFormat()))
                    .displayOrder(nextOrder + i)
                    .build();
            post.addMedia(media);
        }

        // Cập nhật tags nếu được gửi lên
        if (request.getTagNames() != null) {
            post.getPostTags().clear();
            Set<Tag> tags = resolveOrCreateTags(request.getTagNames());
            tags.forEach(tag -> post.getPostTags().add(
                    PostTag.builder()
                            .id(new PostTagId(post.getId(), tag.getId()))
                            .post(post).tag(tag).build()));
        }

        // Cập nhật sources nếu được gửi lên
        if (request.getSources() != null) {
            post.getSources().clear();
            request.getSources().stream()
                    .map(postMapper::toSourceEntity)
                    .forEach(post::addSource);
        }

        return postRepository.save(post);
    }

    // DELETE

    // Xóa bài viết — dọn media trên Cloudinary TRƯỚC khi xóa DB.
    @Transactional
    public void deletePost(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }

        // Xóa media trên Cloudinary
        // log lỗi nhưng không rollback để tránh data zombie trong DB
        List<PostMedia> mediaList = postMediaRepository.findByPostIdOrderByDisplayOrder(postId);
        mediaList.forEach(media -> {
            try {
                cloudinaryService.deleteFile(media.getPublicId(), media.getResourceType());
                log.debug("Deleted Cloudinary asset: {}", media.getPublicId());
            } catch (AppException e) {
                log.warn("Failed to delete Cloudinary asset {}: {}", media.getPublicId(), e.getMessage());
                // Không throw — tiếp tục xóa file còn lại và DB record
            }
        });

        // Xóa mềm post trong DB (cascade xóa media, source, tag records)
        postRepository.delete(post); // trigger @SQLDelete → set deleted_at

        // Publish event
        eventPublisher.publishEvent(new PostDeletedEvent(this, postId, currentUserId));

        log.info("Post deleted: postId={}, by={}", postId, currentUserId);
    }

    // Private helpers

    // Upload danh sách file, trả về list UploadResult.
    // Nếu một file thất bại, rollback toàn bộ file đã upload thành công.
    private List<UploadResult> uploadFilesWithRollback(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files))
            return List.of();

        List<UploadResult> results = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            try {
                results.add(cloudinaryService.uploadFile(file));
            } catch (AppException e) {
                // Rollback các file đã upload thành công
                for (UploadResult r : results) {
                    try {
                        cloudinaryService.deleteFile(
                                r.getPublicId(),
                                r.getResourceType());
                    } catch (Exception ce) {
                        log.error("CRITICAL: rollback delete failed, leaked file publicId={}", r.getPublicId(), ce);
                    }
                }
            }
        }
        return results;
    }

    // Xóa media bị đánh dấu trong UpdateRequest.
    private void deleteRequestedMedia(PostUpdateRequest request, Post post) {
        if (CollectionUtils.isEmpty(request.getRemoveMediaPublicIds()))
            return;

        request.getRemoveMediaPublicIds().forEach(publicId -> {
            cloudinaryService.deleteFile(
                    publicId,
                    post.getMediaList().stream()
                            .filter(m -> m.getPublicId().equals(publicId))
                            .map(PostMedia::getResourceType)
                            .findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.DELETE_MEDIA_FAILED)));
            post.getMediaList().removeIf(m -> m.getPublicId().equals(publicId));
        });
    }

    // Tìm tag theo tên, tạo mới nếu chưa tồn tại (upsert).
    private Set<Tag> resolveOrCreateTags(Set<String> tagNames) {
        Set<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName).collect(Collectors.toSet());

        Set<Tag> newTags = tagNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> tagRepository.save(Tag.builder().name(name).build()))
                .collect(Collectors.toSet());

        existingTags.addAll(newTags);
        return existingTags;
    }
}