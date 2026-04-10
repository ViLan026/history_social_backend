package com.example.history_social_backend.core.exception;

import com.example.history_social_backend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Lỗi do t định nghĩa trong lúc code (không phải lỗi framework tự ném ra)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(
            AppException exception,
            HttpServletRequest request) {

        ErrorCode errorCode = exception.getErrorCode();

        log.warn("Business error at [{}]: {}", request.getRequestURI(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(buildErrorResponse(errorCode, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        // Lấy lỗi đầu tiên xuất hiện trong quá trình validate
        org.springframework.validation.FieldError fieldError = exception.getBindingResult().getFieldError();

        // Đặt giá trị mặc định nếu có lỗi không xác định
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String finalMessage = errorCode.getMessage();

        if (fieldError != null) {
            String enumKey = fieldError.getDefaultMessage(); // VD: lấy ra "EMAIL_IS_BLANK" hoặc "must not be null"

            try {
                // TRƯỜNG HỢP 1: Chữ trong DTO khớp với Enum (VD: "EMAIL_IS_BLANK")
                errorCode = ErrorCode.valueOf(enumKey);
                finalMessage = errorCode.getMessage(); // Lấy được câu tiếng Việt

                // Map thêm các biến {min}, {max} nếu có
                ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);
                Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
                finalMessage = mapAttribute(finalMessage, attributes);

            } catch (IllegalArgumentException e) {
                // TRƯỜNG HỢP 2: Chữ trong DTO là chuỗi bình thường không nằm trong Enum
                // Nó sẽ rớt vào catch này. Ta vẫn dùng mã mặc định 1000,
                // nhưng lấy nội dung text đó làm message trả về luôn.
                finalMessage = fieldError.getDefaultMessage();
            } catch (Exception e) {
                log.debug("Cannot extract validation attributes for field {}", fieldError.getField());
            }
        }

        log.warn("Validation error at [{}]: {}", request.getRequestURI(), finalMessage);

        // Build cục JSON trả về trực tiếp Mã code và Message của lỗi cụ thể
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Void>builder()
                        .success(false)
                        .code(errorCode.getCode()) // Lấy mã số chính xác (VD: 1004, 1008...)
                        .message(finalMessage) // Lấy câu thông báo chính xác
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // Hàm phụ để thay thế các placeholder trong message bằng giá trị thực tế từ
    // attributes
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String result = message;

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() != null) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue().toString());
            }
        }
        return result;
    }

    // Lỗi dùng sai http method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {

        log.warn("Method not allowed at [{}]: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(buildErrorResponse(ErrorCode.METHOD_NOT_ALLOWED, null));
    }

    // Lỗi phân quyền
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {

        log.warn("Access denied at [{}]: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNAUTHORIZED.getHttpStatus())
                .body(buildErrorResponse(ErrorCode.UNAUTHORIZED, null));
    }

    // lỗi tham số không hợp lệ
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        log.warn("Illegal argument at [{}]: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(buildErrorResponse(ErrorCode.INVALID_REQUEST,
                        List.of(exception.getMessage())));
    }

    // Các lỗi chưa định nghĩa trong AppException sẽ được xử lý ở đây
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknownException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error at [{}]", request.getRequestURI(), exception);

        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus())
                .body(buildErrorResponse(ErrorCode.UNCATEGORIZED_EXCEPTION, null));
    }

    // hàm tiện ích để xây dựng response lỗi thống nhất
    private ApiResponse<Void> buildErrorResponse(ErrorCode errorCode, List<String> errors) {

        return ApiResponse.<Void>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}