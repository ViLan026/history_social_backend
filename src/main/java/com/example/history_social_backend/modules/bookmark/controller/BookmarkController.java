package com.example.history_social_backend.modules.bookmark.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkCountResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkStatusResponse;
import com.example.history_social_backend.modules.bookmark.service.BookmarkService;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkToggleResponse;

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
	public ApiResponse<BookmarkToggleResponse> toggleBookmark(
			@PathVariable UUID postId) {

		return ApiResponse.success(
				bookmarkService.toggleBookmark( postId));
	}

	@GetMapping
	public ApiResponse<PageResponse<BookmarkResponse>> getBookmarkedPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ApiResponse.success(
				bookmarkService.getBookmarkedPosts(page, size));
	}

	@GetMapping("/check/{postId}")
	public ApiResponse<BookmarkStatusResponse> checkBookmarkStatus(
			@PathVariable UUID postId) {

		boolean isBookmarked = bookmarkService.isBookmarked(postId);

		return ApiResponse.success(
				BookmarkStatusResponse.builder()
						.postId(postId)
						.bookmarked(isBookmarked)
						.build());
	}

	@GetMapping("/count")
	public ApiResponse<BookmarkCountResponse> getBookmarkCount() {

		long count = bookmarkService.getBookmarkCount();

		return ApiResponse.success(
				BookmarkCountResponse.builder()
						.totalBookmarks(count)
						.build());
	}
}