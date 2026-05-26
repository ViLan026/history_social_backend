package com.example.history_social_backend.modules.user.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.user.dto.response.RoleResponse;
import com.example.history_social_backend.modules.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ROLES)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable UUID id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

}