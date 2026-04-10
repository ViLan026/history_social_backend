package com.example.history_social_backend.modules.user.domain;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class AppPermission extends BaseEntity {

   @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    UUID id; // Giữ ID là UUID

    @Column(nullable = false, unique = true, length = 50)
    String name; // Tên quyền thực tế: "CREATE_POST", "UPDATE_USER"...

    @Column(name = "description", length = 255)
    String description;
}
