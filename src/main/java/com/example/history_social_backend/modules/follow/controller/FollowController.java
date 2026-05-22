package com.example.history_social_backend.modules.follow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.follow.dto.response.FollowResponse;
import com.example.history_social_backend.modules.follow.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.FOLLOWS)
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ApiResponse<Void> followUser(@PathVariable UUID userId) {
        followService.followUser(userId);
        return ApiResponse.success("Follow user successfully");
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> unfollowUser(@PathVariable UUID userId) {
        followService.unfollowUser(userId);
        return ApiResponse.success("Unfollow user successfully");
    }

    @GetMapping("/{userId}/followers")
    public ApiResponse<PageResponse<FollowResponse>> getFollowers(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/{userId}/following")
    public ApiResponse<PageResponse<FollowResponse>> getFollowing(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(followService.getFollowing(userId, pageable));
    }

    @GetMapping("/suggestions")
    public ApiResponse<List<FollowResponse>> getFollowSuggestions(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<FollowResponse>>builder()
                .success(true)
                .message("Get follow suggestions successfully")
                .data(followService.getFollowSuggestions(limit))
                .build();
    }
}