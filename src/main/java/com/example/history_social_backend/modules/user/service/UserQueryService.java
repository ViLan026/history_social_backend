package com.example.history_social_backend.modules.user.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.mapper.UserMapper;
import com.example.history_social_backend.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = findUserWithDetails(id);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public String getUserName(UUID id) {
        String name = userRepository.findUsernameByUserId(id);
        return name;
    }

    protected User findUserWithDetails(UUID id) {
        return userRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // Trả về Map để Module Reaction dễ dàng lấy thông tin theo UUID
    public Map<UUID, UserReactionResponse> getUserReactionInfoMap(Set<UUID> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream().collect(Collectors.toMap(
                User::getId,
                user -> UserReactionResponse.builder()
                        .userId(user.getId())
                        .displayName(user.getProfile() != null ? user.getProfile().getDisplayName() : "Unknown")
                        .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null)
                        .build()));
    }

    public UserReactionResponse getUserInfo(UUID id) {
        User user = findById(id);
        return UserReactionResponse.builder()
                .userId(user.getId())
                .displayName(user.getProfile() != null ? user.getProfile().getDisplayName() : "Unknown")
                .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null)
                .build();
    }

}
