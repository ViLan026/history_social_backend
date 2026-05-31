package com.example.history_social_backend.modules.follow.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.history_social_backend.modules.follow.domain.Follow;

public interface FollowRepository
        extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerIdAndFollowingId(
            UUID followerId,
            UUID followingId);

    Optional<Follow> findByFollowerIdAndFollowingId(
            UUID followerId,
            UUID followingId);

    Page<Follow> findAllByFollowerId(UUID followerId, Pageable pageable);

    Page<Follow> findAllByFollowingId(UUID followingId, Pageable pageable);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    List<Follow> findByFollowerId(UUID followerId);

    @Query("""
                SELECT f.followingId
                FROM Follow f
                WHERE f.followerId = :userId
            """)
    List<UUID> findFollowingIdsByFollowerId(@Param("userId") UUID userId);

    @Query("""
                SELECT f.followingId
                FROM Follow f
                WHERE f.followerId IN :followingIds
                AND f.followingId <> :currentUserId
                AND f.followingId NOT IN :excludedIds
                GROUP BY f.followingId
                ORDER BY COUNT(f.followingId) DESC
            """)
    List<UUID> findSuggestedUserIdsByMutualFollowing(
            @Param("currentUserId") UUID currentUserId,
            @Param("followingIds") List<UUID> followingIds,
            @Param("excludedIds") List<UUID> excludedIds,
            Pageable pageable);

    @Query("""
                SELECT f.followingId
                FROM Follow f
                WHERE f.followingId <> :currentUserId
                GROUP BY f.followingId
                ORDER BY COUNT(f.followerId) DESC
            """)
    List<UUID> findPopularUserIds(
            @Param("currentUserId") UUID currentUserId,
            Pageable pageable);
}