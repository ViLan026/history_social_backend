package com.example.history_social_backend.modules.post.repository;


import com.example.history_social_backend.modules.post.domain.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {

    List<PostMedia> findByPostIdOrderByDisplayOrder(UUID postId);

    /** Xóa toàn bộ media của một bài viết — dùng khi delete post. */
    void deleteByPostId(UUID postId);
}