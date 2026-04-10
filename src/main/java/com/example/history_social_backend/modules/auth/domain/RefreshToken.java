package com.example.history_social_backend.modules.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

import com.example.history_social_backend.common.domain.BaseEntity;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    @Id
    private String id; // jti(JWD ID) của JWT

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "expiry_time", nullable = false)
    private Date expiryTime;   // Thời gian hết hạn của refresh token

    @Column(nullable = false)
    private boolean revoked;  // Trạng thái của refresh token (đã bị thu hồi hay chưa)
}