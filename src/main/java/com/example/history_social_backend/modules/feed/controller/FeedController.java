package com.example.history_social_backend.modules.feed.controller;

import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.feed.dto.FeedPostResponse;
import com.example.history_social_backend.modules.feed.service.FeedService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ApiResponse<PageResponse<FeedPostResponse>> getFeed(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<FeedPostResponse> page = feedService.getFeed(pageable);

        return ApiResponse.success(PageResponse.from(page));
    }
}