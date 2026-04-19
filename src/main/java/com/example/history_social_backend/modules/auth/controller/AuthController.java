package com.example.history_social_backend.modules.auth.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.auth.dto.request.AuthenticationRequest;
import com.example.history_social_backend.modules.auth.dto.response.AuthenticationResponse;
import com.example.history_social_backend.modules.auth.dto.response.TokenPair;
import com.example.history_social_backend.modules.auth.service.AuthenticationService;
import com.example.history_social_backend.modules.user.dto.request.UserCreationRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody @Valid AuthenticationRequest request,
            HttpServletResponse response) {
        TokenPair tokens = authService.login(request);

        addAuthCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Login successful")
                        .data(AuthenticationResponse.builder()
                                .authenticated(true)
                                .build())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    private void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false) // true khi deploy HTTPS
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken);
        clearAuthCookies(response);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Logout successful")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // @PostMapping("/introspect")
    // public ApiResponse<IntrospectResponse> introspect(
    //         @RequestBody @Valid IntrospectRequest request) {

    //     return ApiResponse.<IntrospectResponse>builder()
    //             .code(HttpStatus.OK.value())
    //             .message(null)
    //             .data(authService.introspect(request))
    //             .build();
    // }

    // @PostMapping("/refresh")
    // public ApiResponse<AuthenticationResponse> refresh(
    // @RequestBody @Valid RefreshRequest request) {

    // return ApiResponse.<AuthenticationResponse>builder()
    // .code(HttpStatus.OK.value())
    // .message("Token refreshed")
    // .data(authService.refresh(request))
    // .build();
    // }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        TokenPair tokens = authService.refresh(refreshToken);

        addAuthCookies(
                response,
                tokens.getAccessToken(),
                tokens.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Token refreshed")
                        .data(
                                AuthenticationResponse.builder()
                                        .authenticated(true)
                                        .message("Token refreshed successfully")
                                        .build())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}