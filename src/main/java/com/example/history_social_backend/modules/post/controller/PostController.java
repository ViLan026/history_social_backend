package com.example.history_social_backend.modules.post.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.post.dto.request.PostCreationRequest;
import com.example.history_social_backend.modules.post.dto.request.PostUpdateRequest;
import com.example.history_social_backend.modules.post.dto.request.UpdateMyPostStatusRequest;
import com.example.history_social_backend.modules.post.dto.response.FeedPostResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckDetailResponse;
import com.example.history_social_backend.modules.post.dto.response.PostFactCheckPreviewResponse;
import com.example.history_social_backend.modules.post.dto.response.PostResponse;
import com.example.history_social_backend.modules.post.dto.response.UpdatePostStatusResponse;
import com.example.history_social_backend.modules.post.service.PostFactCheckService;
import com.example.history_social_backend.modules.post.service.PostQueryService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.POSTS)
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostQueryService postQueryService;
    private final PostFactCheckService postFactCheckService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestPart("post") @Valid PostCreationRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        PostResponse response = postService.createPost(request, files);

        return ApiResponse.success(response);
    }

    // xem chi tiết bài viết
    @GetMapping("/{id}")
    public ApiResponse<FeedPostResponse> getPost(@PathVariable UUID id) {
        return ApiResponse.success(postQueryService.getPostById(id));
    }

    @GetMapping("/home")
    public ApiResponse<PageResponse<FeedPostResponse>> getPublicHomePosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<FeedPostResponse> pageData = postQueryService.getPublicPublishedPosts(pageable);

        return ApiResponse.success(PageResponse.from(pageData));
    }

    // trang chủ khi người dùng đã đăng nhập
    @GetMapping
    public ApiResponse<PageResponse<FeedPostResponse>> getPublishedPosts(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<FeedPostResponse> pageData = postQueryService.getPublishedPosts(pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    // xem danh sách bài viết của một tác giả nào đó
    @GetMapping("/author/{authorId}")
    public ApiResponse<PageResponse<FeedPostResponse>> getPostsByAuthor(
            @PathVariable UUID authorId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<FeedPostResponse> pageData = postQueryService.getPostsByAuthor(authorId, pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @GetMapping("/me")
    public ApiResponse<PageResponse<FeedPostResponse>> getMyPosts(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<FeedPostResponse> pageData = postQueryService.getMyPosts(pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    //
    @GetMapping("/search")
    public ApiResponse<PageResponse<FeedPostResponse>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<FeedPostResponse> pageData = postQueryService.searchPosts(keyword, pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(
            @PathVariable UUID id,
            @RequestPart("post") @Valid PostUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        PostResponse response = postService.updatePost(id, request, files);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{postId}/status")
    public ApiResponse<UpdatePostStatusResponse> updateMyPostStatus(
            @PathVariable UUID postId,
            @Valid @RequestBody UpdateMyPostStatusRequest request) {

        UpdatePostStatusResponse response = postService.updateMyPostStatus(
                postId,
                request.getStatus());

        return ApiResponse.success(response);
    }

    @GetMapping("/{id}/fact-check/preview")
    public ApiResponse<PostFactCheckPreviewResponse> getFactCheckPreview(
            @PathVariable UUID id) {
        return ApiResponse.success(postFactCheckService.getFactCheckPreview(id));
    }

    @GetMapping("/{id}/fact-check/detail")
    public ApiResponse<PostFactCheckDetailResponse> getFactCheckDetail(
            @PathVariable UUID id) {
        return ApiResponse.success(postFactCheckService.getFactCheckDetail(id));
    }

}