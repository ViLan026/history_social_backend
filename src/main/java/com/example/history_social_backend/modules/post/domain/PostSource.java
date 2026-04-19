package com.example.history_social_backend.modules.post.domain;

import java.util.UUID;

import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "post_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSource {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    Post post;

    @Column(nullable = false, length = 500)
    String title;               // tên nguồn / tên sách

    @Column(length = 1000)
    String url;                 // URL nếu là nguồn online

    @Column(length = 300)
    String authorName;              // tác giả tài liệu

    @Column(name = "published_year")
    Integer publishedYear;
}