package com.example.history_social_backend.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.auth.domain.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    // tìm theo user
    List<RefreshToken> findByUserId(UUID userId);

    // tìm token hợp lệ (chưa revoke)
    Optional<RefreshToken> findByIdAndRevokedFalse(String id);

    // xoá token hết hạn (cleanup)
    void deleteByExpiryTimeBefore(java.util.Date now);

    // revoke tất cả token của user (logout all devices)
    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);
}