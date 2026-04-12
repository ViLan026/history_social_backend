package com.example.history_social_backend.modules.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.user.domain.AppPermission;

@Repository
public interface PermissionRepository extends JpaRepository<AppPermission, UUID> {
}