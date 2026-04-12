package com.example.history_social_backend.modules.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken extends BaseEntity {

    @Id
    String id; // jti(JWD ID) của JWT

    @Column(name = "user_id", nullable = false)
    UUID userId;

    @Column(name = "expiry_time", nullable = false)
    Date expiryTime;   // Thời gian hết hạn của refresh token

    @Column(nullable = false)
    boolean revoked;  // Trạng thái của refresh token (đã bị thu hồi hay chưa)
}