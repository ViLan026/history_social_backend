package com.example.history_social_backend.modules.follow.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.history_social_backend.modules.follow.domain.Follow;

public interface FollowRepository
    extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerIdAndFollowingId(
        UUID followerId,
        UUID followingId
    );

    Optional<Follow> findByFollowerIdAndFollowingId(
        UUID followerId,
        UUID followingId
    );
    Page<Follow> findAllByFollowerId(UUID followerId, Pageable pageable);

    Page<Follow> findAllByFollowingId(UUID followingId, Pageable pageable);

}