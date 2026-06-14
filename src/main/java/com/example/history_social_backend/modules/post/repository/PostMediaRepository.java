package com.example.history_social_backend.modules.post.repository;

import com.example.history_social_backend.modules.post.domain.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {

    List<PostMedia> findByPostIdOrderByDisplayOrder(UUID postId);

    // chỉ cập nhật cột deleteAt
    void deleteByPostId(UUID postId);
}