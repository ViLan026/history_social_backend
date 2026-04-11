package com.example.history_social_backend.modules.post.service;

import com.example.history_social_backend.common.event.PostCreatedEvent;
import com.example.history_social_backend.common.event.PostDeletedEvent;
import com.example.history_social_backend.common.event.PostUpdatedEvent;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.media.internal.UploadResult;
import com.example.history_social_backend.modules.media.service.CloudinaryService;
import com.example.history_social_backend.modules.post.domain.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final TagService tagService;

    // Thread pool riêng cho upload (max 10 concurrent uploads)
    private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(10);

    @Autowired
    @Lazy
    private PostService self;

    public boolean existsById(UUID id) {
        return postRepository.existsById(id);
    }

    // ================= CREATE =================
    // flow: Upload file → Validate → Resolve Tag → Create Post → Attach relations →
    // Save → Publish event

    public PostResponse createPost(PostCreationRequest request, List<MultipartFile> files, UUID authorId) {
        // Service tự quyết định folder structure
        String folderName = getPostFolderForUser(authorId);

        // 1. Upload files đa luồng lên Cloudinary
        List<UploadResult> uploadResults = uploadFilesConcurrentlyWithRollback(files, folderName);

        try {
            // 2. Lưu DB qua 'self' để giữ được @Transactional
            Post savedPost = self.savePostWithinTransaction(request, uploadResults, authorId);

            // 3. Publish event
            eventPublisher.publishEvent(new PostCreatedEvent(
                    this, savedPost.getId(), authorId, savedPost.getTitle()));

            return postMapper.toPostResponse(savedPost);

        } catch (Exception e) {
            // 4. Rollback file trên Cloudinary không đồng bộ (non-blocking)
            log.warn("Lưu DB thất bại, tiến hành xóa bù trừ {} file đã upload", uploadResults.size());
            CompletableFuture.runAsync(() -> rollbackUploadedFiles(uploadResults), uploadExecutor);
            throw e;
        }
    }

    @Transactional
    protected Post savePostWithinTransaction(PostCreationRequest request, List<UploadResult> uploadResults,
            UUID authorId) {

        // Tạo Post (chưa save)
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(authorId)
                .status(request.getStatus())
                .build();

        // media
        for (int i = 0; i < uploadResults.size(); i++) {
            UploadResult ur = uploadResults.get(i);
            PostMedia media = PostMedia.builder()
                    .mediaUrl(ur.getMediaUrl())
                    .publicId(ur.getPublicId())
                    .resourceType(ur.getResourceType())
                    .mediaType(cloudinaryService.resolveMediaType(ur.getResourceType()))
                    .displayOrder(i)
                    .build();
            post.addMedia(media);
        }

        // sources
        if (!CollectionUtils.isEmpty(request.getSources())) {
            request.getSources().stream()
                    .map(postMapper::toSourceEntity)
                    .forEach(post::addSource);
        }

        // Resolve Tag
        Set<Tag> tags = Collections.emptySet();
        if (!CollectionUtils.isEmpty(request.getTagNames())) {
            tags = tagService.resolveOrCreateTags(request.getTagNames());
        }

        // SAVE POST TRƯỚC (để có ID)
        Post savedPost = postRepository.save(post);

        // Tạo PostTag (KHÔNG set id thủ công)
        for (Tag tag : tags) {
            PostTag pt = PostTag.builder()
                    .post(savedPost)
                    .tag(tag)
                    .createdAt(LocalDateTime.now())
                    .build();

            savedPost.getPostTags().add(pt);
        }

        // 7. Update usageCount
        tagService.increaseUsageCount(tags);

        return savedPost;
    }

    // ================= READ =================

    @Transactional
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        post.getSources().size();

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

    // ================= UPDATE =================
    // Flow: Upload → Transaction DB → After commit → delete file cũ → If fail →
    // rollback file mới
    public PostResponse updatePost(UUID postId, PostUpdateRequest request, List<MultipartFile> newFiles,
            UUID currentUserId) {
        Post post = postRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }

        String folderName = getPostFolderForUser(currentUserId);

        // Upload file mới trước transaction
        List<UploadResult> newUploads = uploadFilesConcurrentlyWithRollback(newFiles, folderName);

        // Chuẩn bị danh sách file cần xóa
        Map<String, String> filesToDelete = new HashMap<>();
        if (!CollectionUtils.isEmpty(request.getRemoveMediaPublicIds())) {
            post.getMediaList().stream()
                    .filter(m -> request.getRemoveMediaPublicIds().contains(m.getPublicId()))
                    .forEach(m -> filesToDelete.put(m.getPublicId(), m.getResourceType()));
        }

        try {
            Post updated = self.applyUpdateWithinTransaction(post, request, newUploads);

            if (!filesToDelete.isEmpty()) {
                deleteCloudinaryFiles(filesToDelete); // Gọi hàm batch delete
            }

            eventPublisher.publishEvent(new PostUpdatedEvent(
                    this, updated.getId(), currentUserId, "Post content updated"));

            return postMapper.toPostResponse(updated);
        } catch (Exception e) {
            log.warn("Update DB thất bại, tiến hành dọn dẹp các file mới tải lên...");
            CompletableFuture.runAsync(() -> rollbackUploadedFiles(newUploads), uploadExecutor);
            throw e;
        }
    }

    @Transactional
    protected Post applyUpdateWithinTransaction(Post post, PostUpdateRequest request, List<UploadResult> newUploads) {
        if (request.getTitle() != null)
            post.setTitle(request.getTitle());
        if (request.getContent() != null)
            post.setContent(request.getContent());
        if (request.getStatus() != null)
            post.setStatus(request.getStatus());

        // Xóa media TRONG transaction
        if (!CollectionUtils.isEmpty(request.getRemoveMediaPublicIds())) {
            post.getMediaList().removeIf(m -> request.getRemoveMediaPublicIds().contains(m.getPublicId()));
        }

        // Thêm media 
        int nextOrder = post.getMediaList().size();
        for (int i = 0; i < newUploads.size(); i++) {
            UploadResult ur = newUploads.get(i);
            PostMedia media = PostMedia.builder()
                    .mediaUrl(ur.getMediaUrl())
                    .publicId(ur.getPublicId())
                    .resourceType(ur.getResourceType())
                    .mediaType(cloudinaryService.resolveMediaType(ur.getResourceType())) // ✅ FIX
                    .displayOrder(nextOrder + i)
                    .build();
            post.addMedia(media);
        }

        // Update Tag (FIX CHUẨN)
        if (request.getTagNames() != null) {

            post.getPostTags().clear();

            Set<Tag> tags = tagService.resolveOrCreateTags(request.getTagNames());

            for (Tag tag : tags) {
                PostTag pt = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .createdAt(LocalDateTime.now())
                        .build();

                post.getPostTags().add(pt);
            }

            tagService.increaseUsageCount(tags);
        }

        // Update sources
        if (request.getSources() != null) {
            post.getSources().clear();

            request.getSources().stream()
                    .map(postMapper::toSourceEntity)
                    .forEach(post::addSource);
        }

        return post;
    }

    // ================= DELETE =================

    @Transactional
    public void deletePost(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new AppException(ErrorCode.POST_FORBIDDEN);
        }

        List<PostMedia> mediaList = postMediaRepository.findByPostIdOrderByDisplayOrder(postId);

        postRepository.delete(post);

        // Dùng Batch Delete
        CompletableFuture.runAsync(() -> {
            Map<String, List<String>> groupedByType = mediaList.stream()
                    .collect(Collectors.groupingBy(
                            PostMedia::getResourceType,
                            Collectors.mapping(PostMedia::getPublicId, Collectors.toList())));

            groupedByType.forEach((resourceType, publicIds) -> {
                try {
                    cloudinaryService.deleteFiles(publicIds, resourceType);
                    log.debug("Batch deleted Cloudinary assets type: {}", resourceType);
                } catch (Exception e) {
                    log.warn("Failed to batch delete type {}: {}", resourceType, e.getMessage());
                }
            });
        }, uploadExecutor);

        eventPublisher.publishEvent(new PostDeletedEvent(this, postId, currentUserId));
        log.info("Post deleted: postId={}, by={}", postId, currentUserId);
    }

    // ================= PRIVATE HELPERS =================

    private List<UploadResult> uploadFilesConcurrentlyWithRollback(List<MultipartFile> files, String folderName) {
        if (CollectionUtils.isEmpty(files))
            return List.of();

        List<CompletableFuture<UploadResult>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(
                        () -> cloudinaryService.uploadFile(file, folderName),
                        uploadExecutor // Dùng thread pool riêng
                ))
                .toList();

        try {
            return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } catch (Exception e) {
            List<UploadResult> successfulUploads = futures.stream()
                    .filter(f -> f.isDone() && !f.isCompletedExceptionally())
                    .map(CompletableFuture::join)
                    .toList();

            log.error("Một số file tải lên bị lỗi. Thực hiện rollback {} file đã tải lên trước đó.",
                    successfulUploads.size());
            rollbackUploadedFiles(successfulUploads);

            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    // Dùng Batch Delete thay vì vòng lặp For
    private void rollbackUploadedFiles(List<UploadResult> uploadedFiles) {
        if (CollectionUtils.isEmpty(uploadedFiles))
            return;

        Map<String, List<String>> groupedByType = uploadedFiles.stream()
                .collect(Collectors.groupingBy(
                        UploadResult::getResourceType,
                        Collectors.mapping(UploadResult::getPublicId, Collectors.toList())));

        groupedByType.forEach((type, ids) -> {
            try {
                cloudinaryService.deleteFiles(ids, type);
            } catch (Exception ce) {
                log.error("CRITICAL: rollback batch delete failed for type {}: {}", type, ce.getMessage());
            }
        });
    }

    // Chỉ xóa file trên Cloudinary, KHÔNG touch entity
    // Nhận Map<PublicId, ResourceType> và dùng Batch Delete
    private void deleteCloudinaryFiles(Map<String, String> filesToDelete) {
        if (CollectionUtils.isEmpty(filesToDelete))
            return;

        CompletableFuture.runAsync(() -> {
            Map<String, List<String>> groupedByType = filesToDelete.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            Map.Entry::getValue,
                            Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

            groupedByType.forEach((type, ids) -> {
                try {
                    cloudinaryService.deleteFiles(ids, type);
                    log.debug("Batch deleted Cloudinary files type {}: {}", type, ids);
                } catch (Exception e) {
                    log.warn("Failed to batch delete type {}: {}", type, e.getMessage());
                }
            });
        }, uploadExecutor);
    }

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

    @PreDestroy
    public void cleanup() {
        uploadExecutor.shutdown();
        log.info("Upload executor shutdown");
    }

    private String getPostFolderForUser(UUID userId) {
        return "history_social" + "posts/" + userId.toString();
    }
}