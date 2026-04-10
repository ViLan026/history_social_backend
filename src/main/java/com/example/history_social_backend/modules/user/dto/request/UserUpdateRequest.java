package com.example.history_social_backend.modules.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    // Username thường có quy tắc: chỉ chứa chữ, số, dấu chấm, dấu gạch dưới, không
    // có khoảng trắng
    @Size(min = 3, max = 30, message = "USERNAME_INVALID_LENGTH")
    @Pattern(regexp = "^[a-zA-Z0-9._ ]+$", message = "USERNAME_INVALID_FORMAT")
    String displayName;

    @Size(min = 3, max = 30, message = "USERNAME_INVALID_LENGTH")
    @Pattern(regexp = "^[a-zA-Z0-9._ ]+$", message = "USERNAME_INVALID_FORMAT")
    String username; 

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "EMAIL_INVALID_FORMAT")
    String email;

    @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
    String avatarUrl;

    String bio;
}