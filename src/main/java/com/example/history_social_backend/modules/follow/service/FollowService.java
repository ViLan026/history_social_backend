package com.example.history_social_backend.modules.follow.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.core.security.SecurityUtils;
import com.example.history_social_backend.modules.follow.domain.Follow;
import com.example.history_social_backend.modules.follow.dto.response.FollowResponse;
import com.example.history_social_backend.modules.follow.repository.FollowRepository;
import com.example.history_social_backend.modules.user.domain.Profile;
import com.example.history_social_backend.modules.user.dto.response.ProfileResponse;
import com.example.history_social_backend.modules.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserQueryService userQueryService;

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowSuggestions(int limit) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        int safeLimit = Math.min(Math.max(limit, 1), 20);

        List<UUID> followingIds = followRepository.findFollowingIdsByFollowerId(currentUserId);

        List<UUID> suggestedUserIds;

        if (followingIds.isEmpty()) {
            suggestedUserIds = followRepository.findPopularUserIds(
                    currentUserId,
                    PageRequest.of(0, safeLimit));
        } else {
            List<UUID> excludedIds = new ArrayList<>();
            excludedIds.add(currentUserId);
            excludedIds.addAll(followingIds);

            suggestedUserIds = followRepository.findSuggestedUserIdsByMutualFollowing(
                    currentUserId,
                    followingIds,
                    excludedIds,
                    PageRequest.of(0, safeLimit));
        }

        if (suggestedUserIds.isEmpty()) {
            return List.of();
        }

        Map<UUID, ProfileResponse> userMap = userQueryService
                .getUsergetUserFollowInfoMap(new HashSet<>(suggestedUserIds));

        return suggestedUserIds.stream()
                .map(userId -> {
                    ProfileResponse user = userMap.get(userId);

                    return FollowResponse.builder()
                            .userId(userId)
                            .username(user != null ? user.getUsername() : null)
                            .displayName(user != null ? user.getDisplayName() : null)
                            .avatarUrl(user != null ? user.getAvatarUrl() : null)
                            .build();
                })
                .toList();
    }

    @Transactional
    public void followUser(UUID followingId) {
        UUID followerId = SecurityUtils.getCurrentUserId();

        if (followerId.equals(followingId)) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        userQueryService.validateUserExists(followingId);

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);

        followRepository.save(follow);

        Profile followerProfile = userQueryService.getProfileByUserId(followerId);
        Profile followingProfile = userQueryService.getProfileByUserId(followingId);

        followerProfile.setFollowingCount(followerProfile.getFollowingCount() + 1);
        followingProfile.setFollowerCount(followingProfile.getFollowerCount() + 1);
    }

    @Transactional
    public void unfollowUser(UUID followingId) {
        UUID followerId = SecurityUtils.getCurrentUserId();

        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);

        Profile followerProfile = userQueryService.getProfileByUserId(followerId);
        Profile followingProfile = userQueryService.getProfileByUserId(followingId);

        followerProfile.setFollowingCount(
                Math.max(0, followerProfile.getFollowingCount() - 1));

        followingProfile.setFollowerCount(
                Math.max(0, followingProfile.getFollowerCount() - 1));
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowResponse> getFollowers(UUID userId, Pageable pageable) {
        userQueryService.validateUserExists(userId);

        Page<Follow> followPage = followRepository.findAllByFollowingId(userId, pageable);

        List<UUID> followerIds = followPage.getContent()
                .stream()
                .map(Follow::getFollowerId)
                .toList();

        Map<UUID, Profile> profileMap = userQueryService.getProfilesByUserIds(followerIds);

        Page<FollowResponse> responsePage = followPage.map(follow -> {
            Profile profile = profileMap.get(follow.getFollowerId());

            return FollowResponse.builder()
                    .userId(profile.getUserId())
                    .username(profile.getUsername())
                    .displayName(profile.getDisplayName())
                    .avatarUrl(profile.getAvatarUrl())
                    .build();
        });

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowResponse> getFollowing(UUID userId, Pageable pageable) {
        userQueryService.validateUserExists(userId);

        Page<Follow> followPage = followRepository.findAllByFollowerId(userId, pageable);

        List<UUID> followingIds = followPage.getContent()
                .stream()
                .map(Follow::getFollowingId)
                .toList();

        Map<UUID, Profile> profileMap = userQueryService.getProfilesByUserIds(followingIds);

        Page<FollowResponse> responsePage = followPage.map(follow -> {
            Profile profile = profileMap.get(follow.getFollowingId());

            return FollowResponse.builder()
                    .userId(profile.getUserId())
                    .username(profile.getUsername())
                    .displayName(profile.getDisplayName())
                    .avatarUrl(profile.getAvatarUrl())
                    .build();
        });

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public long countFollowers(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long countFollowing(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }
}