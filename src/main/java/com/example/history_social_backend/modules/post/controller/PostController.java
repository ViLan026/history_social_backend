package com.example.history_social_backend.modules.post.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.post.dto.request.PostCreationRequest;
import com.example.history_social_backend.modules.post.dto.request.PostUpdateRequest;
import com.example.history_social_backend.modules.post.dto.response.PostResponse;
import com.example.history_social_backend.modules.post.dto.response.PostSummaryResponse;
import com.example.history_social_backend.modules.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.history_social_backend.core.security.SecurityUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.POSTS)
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestPart("post") @Valid PostCreationRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        UUID authorId = SecurityUtils.getCurrentUserId();
        PostResponse response = postService.createPost(request, files, authorId);

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPost(@PathVariable UUID id) {
        return ApiResponse.success(postService.getPostById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<PostSummaryResponse>> getPublishedPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<PostSummaryResponse> pageData = postService.getPublishedPosts(pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @GetMapping("/author/{authorId}")
    public ApiResponse<PageResponse<PostSummaryResponse>> getPostsByAuthor(
            @PathVariable UUID authorId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<PostSummaryResponse> pageData = postService.getPostsByAuthor(authorId, pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<PostSummaryResponse>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<PostSummaryResponse> pageData = postService.searchPosts(keyword, pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(
            @PathVariable UUID id,
            @RequestPart("post") @Valid PostUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();
        PostResponse response = postService.updatePost(id, request, files, currentUserId);
        return ApiResponse.success(response);
    }

}