package com.example.history_social_backend.modules.post.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.post.dto.request.AdminUpdatePostStatusRequest;
import com.example.history_social_backend.modules.post.dto.response.AdminPostDetailResponse;
import com.example.history_social_backend.modules.post.dto.response.AdminPostResponse;
import com.example.history_social_backend.modules.post.service.AdminPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ADMIN_POSTS)
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;

    @GetMapping
    public ApiResponse<PageResponse<AdminPostResponse>> getPosts(
            @RequestParam(required = false) PostStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<AdminPostResponse> pageData = adminPostService.getPosts(status, pageable);
        return ApiResponse.success(PageResponse.from(pageData));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminPostDetailResponse> getPostDetail(@PathVariable UUID id) {
        return ApiResponse.success(adminPostService.getPostDetail(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AdminPostDetailResponse> updatePostStatus(
            @PathVariable UUID id,
            @RequestBody @Valid AdminUpdatePostStatusRequest request
    ) {
        return ApiResponse.success(
                adminPostService.updatePostStatus(id, request.getStatus())
        );
    }
}