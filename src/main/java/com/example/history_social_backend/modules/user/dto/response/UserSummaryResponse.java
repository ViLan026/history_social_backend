package com.example.history_social_backend.modules.user.dto.response;

import com.example.history_social_backend.modules.user.domain.AccountStatus;
import com.example.history_social_backend.modules.user.domain.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummaryResponse {
    UUID id;
    String email;
    String displayName;
    String avatarUrl;
    AccountStatus status;
    Set<Role> roles;
}