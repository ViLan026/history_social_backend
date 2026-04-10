package com.example.history_social_backend.modules.auth.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.auth.dto.request.AuthenticationRequest;
import com.example.history_social_backend.modules.auth.dto.request.IntrospectRequest;
import com.example.history_social_backend.modules.auth.dto.request.LogoutRequest;
import com.example.history_social_backend.modules.auth.dto.request.RefreshRequest;
import com.example.history_social_backend.modules.auth.dto.response.AuthenticationResponse;
import com.example.history_social_backend.modules.auth.dto.response.IntrospectResponse;
import com.example.history_social_backend.modules.auth.service.AuthenticationService;
import com.example.history_social_backend.modules.user.dto.request.UserCreationRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(
            @RequestBody @Valid UserCreationRequest request) {

        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(
            @RequestBody @Valid AuthenticationRequest request) {

        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .data(authService.login(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestBody @Valid LogoutRequest request) {

        authService.logout(request.getRefreshToken());

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Logout successful")
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(
            @RequestBody @Valid IntrospectRequest request) {

        return ApiResponse.<IntrospectResponse>builder()
                .code(HttpStatus.OK.value())
                .message(null)
                .data(authService.introspect(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(
            @RequestBody @Valid RefreshRequest request) {

        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Token refreshed")
                .data(authService.refresh(request))
                .build();
    }
}