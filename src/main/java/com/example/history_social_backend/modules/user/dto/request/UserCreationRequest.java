package com.example.history_social_backend.modules.user.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "EMAIL_IS_BLANK")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "EMAIL_INVALID_FORMAT")
    String email;

    @NotBlank(message = "PASSWORD_IS_BLANK")
    @Size(min = 8, max = 64, message = "PASSWORD_INVALID_LENGTH")
    String password;

    public UserCreationRequest(String email, String password) {
        this.email = email == null ? null : email.strip();
        this.password = password == null ? null : password.strip();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.strip();
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.strip();
    }
}