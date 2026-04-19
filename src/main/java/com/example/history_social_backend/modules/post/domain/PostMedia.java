package com.example.history_social_backend.modules.post.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMedia extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    Post post;

    @Column(name = "media_url", nullable = false, length = 1000)
    String mediaUrl;

    @Column(name = "public_id", nullable = false, length = 500)
    String publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    MediaType mediaType;

    @Column(name = "resource_type", nullable = false, length = 20)
    String resourceType; // dùng cho Cloudinary

    @Column(name = "display_order")
    @Builder.Default
    Integer displayOrder = 0; // thứ tự hiển thị media  

}