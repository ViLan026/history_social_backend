package com.example.history_social_backend.modules.auth.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.auth.domain.RefreshToken;
import com.example.history_social_backend.modules.auth.dto.request.AuthenticationRequest;
import com.example.history_social_backend.modules.auth.dto.request.IntrospectRequest;
import com.example.history_social_backend.modules.auth.dto.request.RefreshRequest;
import com.example.history_social_backend.modules.auth.dto.response.AuthenticationResponse;
import com.example.history_social_backend.modules.auth.dto.response.IntrospectResponse;
import com.example.history_social_backend.modules.auth.repository.RefreshTokenRepository;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.dto.request.UserCreationRequest;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jwt.SignedJWT;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import com.example.history_social_backend.modules.user.domain.AccountStatus;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    UserService userService;
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponse register(UserCreationRequest request) {
        // 1. (Tuỳ chọn) Thực hiện các logic nghiệp vụ riêng của luồng Đăng ký (Auth)
        // Ví dụ:
        // - Kiểm tra Google reCAPTCHA
        // - Ghi log hành vi đăng ký
        // - Xử lý format lại dữ liệu đầu vào trước khi lưu...

        // 2. Gọi UserService để thực hiện việc tạo và lưu User vào Database
        UserResponse newUser = userService.createUser(request);

        // 3. (Tuỳ chọn) Xử lý các logic sau khi tạo thành công
        // Ví dụ:
        // - Bắn Event để gửi email kích hoạt tài khoản
        // - Khởi tạo các dữ liệu mặc định khác cho user mới...

        return newUser;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (user.getStatus() != null && user.getStatus() != AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        try {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            saveRefreshToken(refreshToken, user);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();

        } catch (KeyLengthException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    private void saveRefreshToken(String token, User user) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);

        String jti = jwt.getJWTClaimsSet().getJWTID();
        Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

        RefreshToken entity = RefreshToken.builder()
                .id(jti)
                .userId(user.getId())
                .expiryTime(expiry)
                .revoked(false)      // token chưa bị thu hồi (còn dùng được)
                .build();

        refreshTokenRepository.save(entity);
    }

    // hàm kiểm tra token có hợp lệ, chưa bị thu hồi hay chưa hết hạn
    public IntrospectResponse introspect(IntrospectRequest request) {
        String token = request.getToken();

        try {
            jwtService.verifyAccessToken(token);
            return IntrospectResponse.builder()
                    .valid(true)
                    .build();

        } catch (AppException | JOSEException | ParseException e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    public AuthenticationResponse refresh(RefreshRequest request) {
        try {
            SignedJWT jwt = jwtService.verifyRefreshToken(request.getRefreshToken());

            String jti = jwt.getJWTClaimsSet().getJWTID();

            RefreshToken stored = refreshTokenRepository.findById(jti)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            if (stored.isRevoked())
                throw new AppException(ErrorCode.UNAUTHENTICATED);

            // revoke old refresh token
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);

            User user = userService.findById(stored.getUserId());

            String newAccess = jwtService.generateAccessToken(user);
            String newRefresh = jwtService.generateRefreshToken(user);

            saveRefreshToken(newRefresh, user);

            return AuthenticationResponse.builder()
                    .accessToken(newAccess)
                    .refreshToken(newRefresh)
                    .authenticated(true)
                    .build();

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    public void logout(String refreshToken) {
        try {
            SignedJWT jwt = jwtService.verifyRefreshToken(refreshToken);

            String jti = jwt.getJWTClaimsSet().getJWTID();

            RefreshToken stored = refreshTokenRepository.findById(jti)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            stored.setRevoked(true);
            refreshTokenRepository.save(stored);

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
}
