package com.example.history_social_backend.modules.bookmark.service;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.bookmark.domain.Bookmark;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkToggleResponse;
import com.example.history_social_backend.modules.bookmark.mapper.BookmarkMapper;
import com.example.history_social_backend.modules.bookmark.repository.BookmarkRepository;
import com.example.history_social_backend.modules.post.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookmarkService {

    BookmarkRepository bookmarkRepository;
    PostService postService;
    BookmarkMapper bookmarkMapper;

    @Transactional
    public BookmarkToggleResponse toggleBookmark(UUID postId) {

        UUID userId = SecurityUtils.getCurrentUserId();

        //  check tồn tại
        if (!postService.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        // boolean exists = bookmarkRepository.existsByUserIdAndPostId(userId, postId);
        boolean exists = isBookmarked(postId);

        if (exists) {
            bookmarkRepository.deleteByUserIdAndPostId(userId, postId);

            return BookmarkToggleResponse.builder()
                    .action("REMOVED")
                    .bookmarked(false)
                    .message("Đã gỡ bookmark")
                    .build();
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setPostId(postId);

        bookmarkRepository.save(bookmark);

        return BookmarkToggleResponse.builder()
                .action("ADDED")
                .bookmarked(true)
                .message("Đã bookmark")
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<BookmarkResponse> getBookmarkedPosts(int page, int size) {
        UUID userId = SecurityUtils.getCurrentUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        Page<BookmarkResponse> responsePage = bookmarkPage.map(bookmarkMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(UUID postId) {
        UUID userId = SecurityUtils.getCurrentUserId();

        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public long getBookmarkCount() {
        UUID userId = SecurityUtils.getCurrentUserId();

        return bookmarkRepository.countByUserId(userId);
    }
}