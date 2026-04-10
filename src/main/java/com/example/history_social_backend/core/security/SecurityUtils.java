package com.example.history_social_backend.core.security;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    //  Lấy trực tiếp object Jwt của Spring Security
    public static Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra xem Principal có đúng là object Jwt không
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return jwt;
    }

    // các hàm lấy dự liệu từu Jwt.claims đã đặt lúc buildToken (file JwtService)
    public static UUID getCurrentUserId() {
        Jwt jwt = getJwt();
        String idStr = jwt.getClaimAsString("id"); 
        
        if (idStr == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED); 
        }
        return UUID.fromString(idStr);
    }

    public static String getCurrentUserEmail() {
        // Thuộc tính subject lưu email
        return getJwt().getSubject(); 
    }

    public static String getCurrentUserStatus() {
        return getJwt().getClaimAsString("status");
    }


    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        String targetRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(targetRole));
    }
}