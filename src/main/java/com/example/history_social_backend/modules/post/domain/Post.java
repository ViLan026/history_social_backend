package com.example.history_social_backend.modules.post.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
// tạo index cho các cột 
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_author",  columnList = "author_id"),
        @Index(name = "idx_post_status",  columnList = "status"),
        @Index(name = "idx_post_created", columnList = "created_at")
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    UUID id;

    @JoinColumn(name = "author_id", nullable = false)
    UUID authorId;

    @Column(nullable = false, length = 500)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    PostStatus status = PostStatus.DRAFT;

    @Column(name = "view_count")
    @Builder.Default
    Long viewCount = 0L;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    List<PostMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    Set<PostSource> sources = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    Set<PostTag> postTags = new HashSet<>();

    LocalDateTime deletedAt;

    public void addMedia(PostMedia media) {
        media.setPost(this);
        this.mediaList.add(media);
    }

    public void addSource(PostSource source) {
        source.setPost(this);
        this.sources.add(source);
    }
}