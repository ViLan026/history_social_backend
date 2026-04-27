package com.example.history_social_backend.modules.post.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tag_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(nullable = false, unique = true, length = 100)
    String name;

    @Column(length = 300)
    String description;

    @Builder.Default
    @Column(name = "usage_count", nullable = false)
    Integer usageCount = 0;
}

