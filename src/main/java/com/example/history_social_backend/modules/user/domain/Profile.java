package com.example.history_social_backend.modules.user.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_profiles_user"))
    User user;

    @Column(name = "username", length = 100, nullable = false, unique = true)
    String username;

    @Column(name = "display_name", length = 100)
    String displayName;

    @Column(name = "avatar_url", length = 255)
    String avatarUrl;

    @Column(name = "bio", columnDefinition = "text")
    String bio;
}