package com.example.history_social_backend.modules.feed.service;

import com.example.history_social_backend.modules.feed.dto.FeedPostResponse;
import com.example.history_social_backend.modules.feed.mapper.FeedMapper;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostStatus;
import com.example.history_social_backend.modules.post.repository.PostRepository;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FeedMapper feedMapper;           

    @Transactional(readOnly = true)
    public Page<FeedPostResponse> getFeed(Pageable pageable) {
        Page<Post> posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);

        if (posts.isEmpty()) {
            return posts.map(feedMapper::toFeedPostResponse);
        }

        // Lấy danh sách author IDs
        Set<UUID> authorIds = posts.getContent().stream()
                .map(Post::getAuthorId)
                .collect(Collectors.toSet());

        // Fetch users + profile một lần
        List<User> authors = userRepository.findUsersWithProfileByIds(authorIds);
        Map<UUID, User> userMap = authors.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));


        return posts.map(post -> {
            FeedPostResponse response = feedMapper.toFeedPostResponse(post);

            // Xử lý AuthorSummary riêng 
            User author = userMap.get(post.getAuthorId());
            if (author != null) {
                FeedPostResponse.AuthorSummary authorSummary = buildAuthorSummary(author);
                response.setAuthor(authorSummary);
            }

            return response;
        });
    }


    private FeedPostResponse.AuthorSummary buildAuthorSummary(User user) {
        boolean hasProfile = user.getProfile() != null;

        return FeedPostResponse.AuthorSummary.builder()
                .userId(user.getId())
                .displayName(hasProfile ? user.getProfile().getDisplayName() : "Ẩn danh")
                .avatarUrl(hasProfile ? user.getProfile().getAvatarUrl() : null)
                .build();
    }
}