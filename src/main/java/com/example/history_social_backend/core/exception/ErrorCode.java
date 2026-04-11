package com.example.history_social_backend.core.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1000, "Invalid request (validation failed)", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(1002, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    UNAUTHORIZED(1003, "Unauthorized", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1009, "Forbidden", HttpStatus.FORBIDDEN),
    TOKEN_CREATION_FAILED(1014, "Failed to create token", HttpStatus.INTERNAL_SERVER_ERROR),

    EMAIL_IS_BLANK(1004, "Email không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_FORMAT(1005, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_IS_BLANK(1006, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(1008, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST),

    USERNAME_INVALID_LENGTH(1010, "Tên đăng nhập phải từ 3 đến 30 ký tự", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID_FORMAT(1011, "Tên đăng nhập chỉ được chứa chữ cái, số, dấu chấm (.) và gạch dưới (_), khoảng cách",
            HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1012, "Tên đăng nhập này đã có người sử dụng", HttpStatus.BAD_REQUEST),
    USER_INACTIVE(1013, "Tài khoản người dùng không hoạt động", HttpStatus.FORBIDDEN),
    USER_BANNED(1015, "Tài khoản người dùng đã bị cấm", HttpStatus.FORBIDDEN),

    // ---------------- Modules.User ----------------

    // Validation
    INVALID_EMAIL(2000, "Email is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2001, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(2002, "Required field is missing", HttpStatus.BAD_REQUEST),
    // User
    USER_NOT_FOUND(2003, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(2004, "User already exists with this email", HttpStatus.CONFLICT),
    USER_LOCKED(2005, "User account is locked", HttpStatus.FORBIDDEN),
    // Role
    ROLE_NOT_FOUND(2006, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(2007, "Role already exists", HttpStatus.CONFLICT),
    // Profile
    PROFILE_NOT_FOUND(2008, "Profile not found", HttpStatus.NOT_FOUND),

    // Media Error Codes
    MEDIA_FILE_EMPTY(4001, "File không được rỗng", HttpStatus.BAD_REQUEST),
    MEDIA_UPLOAD_FAILED(4002, "Upload media thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    MEDIA_DELETE_FAILED(4003, "Xóa media thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    MEDIA_INVALID_PUBLIC_ID(4004, "PublicId không hợp lệ", HttpStatus.BAD_REQUEST),
    MEDIA_FILE_TOO_LARGE(4005, "File vượt quá giới hạn cho phép", HttpStatus.BAD_REQUEST),

    // --------------- Modules.Post -------------------
    POST_NOT_FOUND(3000, "Bài viết không tồn tại", HttpStatus.NOT_FOUND),
    POST_FORBIDDEN(3001, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    POST_ALREADY_DELETED(3002, "Bài viết đã bị xóa trước đó", HttpStatus.CONFLICT),

    UPLOAD_FAILED(3003, "Upload file lên Cloudinary thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_MEDIA_FAILED(3004, "Xóa media trên Cloudinary thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE(3005, "Định dạng file không được hỗ trợ", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(3006, "File vượt quá giới hạn dung lượng", HttpStatus.BAD_REQUEST),

    TAG_NOT_FOUND(3007, "Tag không tồn tại", HttpStatus.NOT_FOUND),

    // --------------- Modules.Comment -------------------
    COMMENT_NOT_FOUND(4001, "Không tìm thấy bình luận", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_DELETED(4002, "Bình luận đã bị xóa", HttpStatus.BAD_REQUEST),
    INVALID_COMMENT_CONTENT(4003,
            "Nội dung bình luận không hợp lệ. Bình luận không được để trống hoặc chỉ chứa dấu câu",
            HttpStatus.BAD_REQUEST),
    DELETE_COMMENT_FORBIDDEN(4004, "Bạn không có quyền xóa bình luận này", HttpStatus.FORBIDDEN),

    // --------------- Modules.Notification -------------------
    NOTIFICATION_NOT_FOUND(5001, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),

    // --------------- Modules.OnThisDay -------------------
    ON_THIS_DAY_NOT_FOUND(6001, "Không tìm thấy sự kiện ngày này năm xưa", HttpStatus.NOT_FOUND),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}