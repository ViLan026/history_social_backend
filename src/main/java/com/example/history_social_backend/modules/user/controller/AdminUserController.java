package com.example.history_social_backend.modules.user.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.constant.AppConstants;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.user.dto.request.RoleCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.RoleUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.RoleResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.dto.response.UserSummaryResponse;
import com.example.history_social_backend.modules.user.service.RoleService;
import com.example.history_social_backend.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ADMIN_USERS)
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public PageResponse<UserSummaryResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        size = Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Page<UserSummaryResponse> result = userService.getAllUsers(page, size, keyword);

        return PageResponse.from(result);
    }


    @PatchMapping("/{id}/lock")
    public ApiResponse<UserResponse> lockUser(@PathVariable UUID id) {
        return ApiResponse.success("User locked", userService.lockUser(id));
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<UserResponse> unlockUser(@PathVariable UUID id) {
        return ApiResponse.success("User unlocked", userService.unlockUser(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoleResponse> createRole(
            @Valid @RequestBody RoleCreationRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .code(201)
                .message("Role created successfully")
                .data(roleService.createRole(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return ApiResponse.success("Role updated successfully",
                roleService.updateRole(id, request));
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

}