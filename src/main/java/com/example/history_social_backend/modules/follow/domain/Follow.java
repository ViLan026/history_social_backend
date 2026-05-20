package com.example.history_social_backend.modules.follow.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "follows", uniqueConstraints = { @UniqueConstraint(columnNames = { "follower_id", "following_id" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Follow extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "follower_id", nullable = false)
    UUID followerId;

    @Column(name = "following_id", nullable = false)
    UUID followingId;
}