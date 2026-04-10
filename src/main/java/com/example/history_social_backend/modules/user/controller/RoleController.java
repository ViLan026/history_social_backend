package com.example.history_social_backend.modules.user.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.user.dto.request.RoleCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.RoleUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.RoleResponse;
import com.example.history_social_backend.modules.user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ROLES)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable UUID id) {
        return ApiResponse.success(roleService.getRoleById(id));
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


    // @DeleteMapping("/{id}")
    // public ApiResponse<Void> deleteRole(@PathVariable UUID id) {
    //     roleService.deleteRole(id);
    //     return ApiResponse.<Void>builder()
    //             .code(200)
    //             .message("Role deleted successfully")
    //             .build();
    // }
}