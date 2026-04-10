package com.example.history_social_backend.modules.user.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.constant.AppConstants;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.user.dto.request.ChangePasswordRequest;
import com.example.history_social_backend.modules.user.dto.request.UserCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.UserUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.dto.response.UserSummaryResponse;
import com.example.history_social_backend.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public PageResponse<UserSummaryResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        size = Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Page<UserSummaryResponse> result = userService.getAllUsers(page, size, keyword);

        return PageResponse.from(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(201)
                .message("User created successfully")
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success("Profile updated successfully",
                userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/password")
    public ApiResponse<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(id, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Password changed successfully")
                .build();
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<UserResponse> lockUser(@PathVariable UUID id) {
        return ApiResponse.success("User locked", userService.lockUser(id));
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<UserResponse> unlockUser(@PathVariable UUID id) {
        return ApiResponse.success("User unlocked", userService.unlockUser(id));
    }

    // @DeleteMapping("/{id}")
    // public ApiResponse<Void> deleteUser(@PathVariable UUID id) {
    // userService.deleteUser(id);
    // return ApiResponse.<Void>builder()
    // .code(200)
    // .message("User deleted successfully")
    // .build();
    // }
}
