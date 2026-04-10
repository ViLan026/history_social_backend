package com.example.history_social_backend.modules.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.history_social_backend.modules.user.domain.AppPermission;

public interface PermissionRepository extends JpaRepository<AppPermission, UUID> {
}