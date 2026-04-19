package com.example.history_social_backend.modules.bookmark.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "bookmarks", uniqueConstraints = @UniqueConstraint(columnNames = { "post_id", "user_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bookmark extends BaseEntity {
    @Id
    @UuidV7 
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "post_id", nullable = false)
    UUID postId;

    @Column(name = "user_id", nullable = false)
    UUID userId;
}