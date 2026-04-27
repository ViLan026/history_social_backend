package com.example.history_social_backend.modules.user.domain;

import java.util.Set;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "description", length = 255)
    String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    Set<AppPermission> appPermissions;
}