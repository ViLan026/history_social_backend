package com.example.history_social_backend.core.configuration;

import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
// xử lí lỗi đã được ném ra để có một lỗi rõ ràng hơn cho frontend hiển thị
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // để xử lý lỗi khi người dùng chưa xác thực hoặc token không hợp lệ, trả về lỗi
    // 401 Unauthorized với thông tin lỗi chi tiết trong response body.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
        response.flushBuffer();

    }
}
