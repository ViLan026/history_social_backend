package com.example.history_social_backend.modules.user.service;

import org.springframework.stereotype.Service;

import com.example.history_social_backend.modules.user.domain.AppPermission;
import com.example.history_social_backend.modules.user.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public AppPermission create(String name) {
        AppPermission p = new AppPermission();
        p.setName(name);
        return permissionRepository.save(p);
    }
}