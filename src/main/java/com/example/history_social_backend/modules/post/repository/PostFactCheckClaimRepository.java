package com.example.history_social_backend.modules.post.repository;

import com.example.history_social_backend.modules.post.domain.PostFactCheckClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostFactCheckClaimRepository extends JpaRepository<PostFactCheckClaim, UUID> {

    List<PostFactCheckClaim> findByPostIdOrderByDisplayOrderAsc(UUID postId);

    void deleteByPostId(UUID postId);
}