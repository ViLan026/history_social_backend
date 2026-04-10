package com.example.history_social_backend.core.configuration;

import com.example.history_social_backend.core.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    // private final JwtService jwtService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) {
        try {
            // // kiểm trả token có hợp lệ(kiểm tra cấu trúc token và chữ kí), chưa hết hạn.
            // jwtService.verifyAccessToken(token);

            if (Objects.isNull(nimbusJwtDecoder)) {
                SecretKeySpec secretKey = new SecretKeySpec(JWT_SECRET.getBytes(), "HmacSHA512");
                nimbusJwtDecoder = NimbusJwtDecoder
                        .withSecretKey(secretKey)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }
            return nimbusJwtDecoder.decode(token);

            // } catch (ParseException | JOSEException e) {
            // throw new JwtException("Invalid JWT: " + e.getMessage());


        } catch (JwtException e) {
            // Bắt lỗi giải mã mặc định của Spring (sai chữ ký, sai định dạng, hết hạn
            // exp,...)
            log.error("Lỗi xác thực JWT từ Spring Security: {}", e.getMessage());
            throw e; // Hoặc ném lại nguyên bản e
        } catch (AppException e) {
            log.warn("Token lỗi: {}", e.getErrorCode().getMessage());
            throw new JwtException("Invalid or revoked JWT: " + e.getErrorCode().getMessage());
        }

    }

}