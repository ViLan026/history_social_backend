package com.example.history_social_backend.modules.user.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "app_permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class AppPermission extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(nullable = false, unique = true, length = 50)
    String name; // Tên quyền thực tế: "CREATE_POST", "UPDATE_USER"...

    @Column(name = "description", length = 255)
    String description;
}
