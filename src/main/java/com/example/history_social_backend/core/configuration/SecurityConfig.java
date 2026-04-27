package com.example.history_social_backend.core.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.Cookie;
import org.springframework.util.StringUtils;

import com.example.history_social_backend.common.constant.ApiPaths;

import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // cho phép dùng annotation như @PreAuthorize, @PostAuthorize
public class SecurityConfig {
    // danh sách các endpoint công khai, không cần login
    private final String[] PUBLIC_ENDPOINTS = {
            ApiPaths.AUTH + "/**"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder; // decode và validate JWT

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // BẬT CORS CHO SPRING SECURITY Ở ĐÂY
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());
        // server nhận JWT từ client. Ví dụ request: Authorization: Bearer dgdd....
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(cookieBearerTokenResolver())
                .jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder) // giải mã và kiểm tra token (chữ kí, cấu trúc, thời gian)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable); // API REST không dùng session => tắt Session authentication

        return httpSecurity.build();
    }

    // cho phép gọi API từ domain khác
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Cấu hình origin tĩnh hoặc dùng setAllowedOriginPatterns("*") nếu cần
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
        corsConfiguration.setExposedHeaders(Arrays.asList("X-Auth-Token"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // mặc định có SCOPE_ ở đầu, giờ đổi thành ""
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    private BearerTokenResolver cookieBearerTokenResolver() {
        return request -> {
            // 1. Ưu tiên lấy token từ Cookie
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("access_token".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }

            // 2. (Tuỳ chọn) Dự phòng lấy từ Header nếu bạn vẫn muốn test kiểu cũ
            String header = request.getHeader("Authorization");
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                return header.substring(7);
            }

            return null; // Không tìm thấy token
        };
    }
}
