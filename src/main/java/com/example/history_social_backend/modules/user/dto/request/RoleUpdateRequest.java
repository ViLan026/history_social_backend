package com.example.history_social_backend.modules.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}       