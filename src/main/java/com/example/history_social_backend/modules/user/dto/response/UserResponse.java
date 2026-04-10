package com.example.history_social_backend.modules.user.dto.response;

import com.example.history_social_backend.modules.user.domain.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    UUID id;
    String email;
    AccountStatus status;
    Set<RoleResponse> roles;
    ProfileResponse profile;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}