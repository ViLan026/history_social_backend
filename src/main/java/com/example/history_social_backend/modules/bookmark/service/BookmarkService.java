package com.example.history_social_backend.modules.bookmark.service;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.bookmark.domain.Bookmark;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkToggleResponse;
import com.example.history_social_backend.modules.bookmark.mapper.BookmarkMapper;
import com.example.history_social_backend.modules.bookmark.repository.BookmarkRepository;
import com.example.history_social_backend.modules.post.service.PostService;
import com.example.history_social_backend.modules.user.service.UserService;

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
    UserService userService;

    @Transactional
    public BookmarkToggleResponse toggleBookmark(String email, UUID postId) {

        UUID userId = userService.getUserIdByEmail(email);

        // chỉ check tồn tại
        if (!postService.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        boolean exists = bookmarkRepository.existsByUserIdAndPostId(userId, postId);

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
    public PageResponse<BookmarkResponse> getBookmarkedPosts(String email, int page, int size) {
        UUID userId = userService.getUserIdByEmail(email);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        Page<BookmarkResponse> responsePage = bookmarkPage.map(bookmarkMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String email, UUID postId) {
        UUID userId = userService.getUserIdByEmail(email);

        return bookmarkRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public long getBookmarkCount(String email) {
        UUID userId = userService.getUserIdByEmail(email);

        return bookmarkRepository.countByUserId(userId);
    }
}