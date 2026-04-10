package com.example.history_social_backend.modules.bookmark.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkCountResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkStatusResponse;
import com.example.history_social_backend.modules.bookmark.service.BookmarkService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.BOOKMARKS)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkController {

    BookmarkService bookmarkService;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<BookmarkService.BookmarkToggleResponse> toggleBookmark(
            @PathVariable UUID postId) {

        String email = SecurityUtils.getCurrentUserEmail();

        return ApiResponse.success(
                bookmarkService.toggleBookmark(email, postId)
        );
    }

    @GetMapping
    public ApiResponse<PageResponse<BookmarkResponse>> getBookmarkedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityUtils.getCurrentUserEmail();

        return ApiResponse.success(
                bookmarkService.getBookmarkedPosts(email, page, size)
        );
    }

    @GetMapping("/check/{postId}")
    public ApiResponse<BookmarkStatusResponse> checkBookmarkStatus(
            @PathVariable UUID postId) {

        String email = SecurityUtils.getCurrentUserEmail();

        boolean isBookmarked = bookmarkService.isBookmarked(email, postId);

        return ApiResponse.success(
                BookmarkStatusResponse.builder()
                        .postId(postId)
                        .bookmarked(isBookmarked)
                        .build()
        );
    }

    @GetMapping("/count")
    public ApiResponse<BookmarkCountResponse> getBookmarkCount() {

        String email = SecurityUtils.getCurrentUserEmail();

        long count = bookmarkService.getBookmarkCount(email);

        return ApiResponse.success(
                BookmarkCountResponse.builder()
                        .totalBookmarks(count)
                        .build()
        );
    }
}