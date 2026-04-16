package com.example.history_social_backend.modules.user.service;

import com.example.history_social_backend.common.constant.AppConstants;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.user.domain.AccountStatus;
import com.example.history_social_backend.modules.user.domain.Profile;
import com.example.history_social_backend.modules.user.domain.Role;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.dto.request.ChangePasswordRequest;
import com.example.history_social_backend.modules.user.dto.request.UserCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.UserUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.UserReactionResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.dto.response.UserSummaryResponse;
import com.example.history_social_backend.modules.user.mapper.UserMapper;
import com.example.history_social_backend.modules.user.repository.ProfileRepository;
import com.example.history_social_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = findUserWithDetails(id);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getAllUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage = (keyword == null || keyword.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.searchUsers(keyword.trim(), pageable);

        // Khi gọi .map(), Spring sẽ tự động lặp qua số lượng bản ghi đã lấy được
        return userPage.map(userMapper::toSummaryResponse);
    }

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Set<Role> roles = Set.of(roleService.findRoleByName(AppConstants.USER_ROLE));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(AccountStatus.ACTIVE)
                .roles(roles) // Assign default role(s)
                .build();

        String generatedUsername = generateRandomUsername(request.getEmail());

        Profile profile = Profile.builder()
                .user(user)
                .username(generatedUsername)
                .build();
        user.setProfile(profile);

        User saved = userRepository.save(user);
        log.info("Created user with id={}", saved.getId());
        return userMapper.toResponse(saved);
    }   

    private String generateRandomUsername(String email) {
        // Lấy phần tên trước ký tự '@'
        String baseName = email.substring(0, email.indexOf("@"));

        // Loại bỏ các ký tự đặc biệt (dấu chấm, gạch ngang...), chỉ giữ lại chữ cái và
        // số
        baseName = baseName.replaceAll("[^a-zA-Z0-9]", "");

        // Đảm bảo baseName không bị rỗng sau khi filter (trường hợp email đặc biệt)
        if (baseName.isEmpty()) {
            baseName = "user";
        }

        String username = baseName;
        int maxTries = 5; // Giới hạn số lần thử để tránh vòng lặp vô tận
        int count = 0;

        // Kiểm tra xem username đã tồn tại trong Database chưa
        while (profileRepository.existsByUsername(username) && count < maxTries) {
            // Nếu trùng, thêm một chuỗi 5 ký tự ngẫu nhiên (từ UUID) vào đuôi
            String randomSuffix = UUID.randomUUID().toString().substring(0, 5);
            username = baseName + "_" + randomSuffix;
            count++;
        }

        // Trong trường hợp thử 5 lần vẫn trùng, dùng luôn UUID đầy đủ
        if (profileRepository.existsByUsername(username)) {
            username = baseName + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }

        return username;
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = findUserWithDetails(id);

        // Ensure profile exists (defensive)
        if (user.getProfile() == null) {
            Profile profile = Profile.builder().user(user).build();
            user.setProfile(profile);
        }

        userMapper.updateProfileFromRequest(user.getProfile(), request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != null && user.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user id={}", id);
    }

    @Transactional
    public UserResponse lockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(AccountStatus.INACTIVE);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse unlockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(AccountStatus.ACTIVE);
        return userMapper.toResponse(userRepository.save(user));
    }

    // @Transactional
    // public void deleteUser(UUID id) {
    // if (!userRepository.existsById(id)) {
    // throw new AppException(ErrorCode.USER_NOT_FOUND);
    // }
    // userRepository.deleteById(id);
    // log.info("Deleted user id={}", id);
    // }

    private User findUserWithDetails(UUID id) {
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
                        .id(user.getId())
                        .displayName(user.getProfile() != null ? user.getProfile().getDisplayName() : "Unknown")
                        .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null)
                        .build()));
    }
}
