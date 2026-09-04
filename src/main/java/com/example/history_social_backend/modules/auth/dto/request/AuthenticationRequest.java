package com.example.history_social_backend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, max = 64, message = "PASSWORD_INVALID_LENGTH")
    private String password;

    public AuthenticationRequest(String email, String password) {
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
