package com.example.history_social_backend.modules.post.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
// tạo index cho các cột
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_author", columnList = "author_id"),
        @Index(name = "idx_post_status", columnList = "status"),
        @Index(name = "idx_post_created", columnList = "post_id")
})
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
// tự động gắn thêm điều kiện WHERE deleted_at IS NULL vào mọi câu lệnh SELECT
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "author_id", nullable = false)
    UUID authorId;

    @Column(nullable = false, length = 500)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    PostStatus status = PostStatus.DRAFT;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<PostMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    Set<PostSource> sources = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    Set<PostTag> postTags = new HashSet<>();

    @Column(name = "view_count")
    @Builder.Default
    Long viewCount = 0L;

    // Float feed_score;
    // Float trending_score;

    // @Column(name = "visibility_label", length = 50)
    // String visibilityLabel;

    // @Column(columnDefinition = "TEXT")
    // String summary;

    // @Column(name = "embedding", columnDefinition = "vector(768)")
    // float[] embedding;

    LocalDateTime deletedAt;

    // --- CÁC HÀM QUẢN LÝ POST MEDIA ---
    public void addMedia(PostMedia media) {
        if (this.mediaList == null) {
            this.mediaList = new ArrayList<>();
        }
        this.mediaList.add(media);
        media.setPost(this);
    }

    public void removeMedia(PostMedia media) {
        if (this.mediaList != null) {
            this.mediaList.remove(media);
            media.setPost(null); // Cắt đứt quan hệ để Hibernate biết và tự xóa
        }
    }

    public void addSource(PostSource source) {
        if (this.sources == null) {
            this.sources = new HashSet<>();
        }
        this.sources.add(source);
        source.setPost(this);
    }

    public void removeSource(PostSource source) {
        if (this.sources != null) {
            this.sources.remove(source);
            source.setPost(null); // Cắt đứt quan hệ
        }
    }
}