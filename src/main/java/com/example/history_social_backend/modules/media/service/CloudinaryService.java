package com.example.history_social_backend.modules.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.post.domain.MediaType;
import com.example.history_social_backend.modules.post.dto.internal.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private static final String FOLDER = "history_social/posts";
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024; // 10 MB
    private static final long MAX_VIDEO_BYTES = 100L * 1024 * 1024; // 100 MB

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif");

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/quicktime",
            "video/webm");

    private final Cloudinary cloudinary;

    public UploadResult uploadFile(MultipartFile file) {
        validateFile(file);

        // String contentType = normalizeContentType(file.getContentType());
        // String resourceType = determineResourceType(contentType);

        Map<String, Object> options = new HashMap<>();
        options.put("folder", FOLDER);
        options.put("resource_type", "auto");
        options.put("use_filename", true);
        options.put("unique_filename", true);
        options.put("overwrite", false);
        options.put("invalidate", true);

        File tempFile = null;
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = ".tmp";

            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            tempFile = File.createTempFile("upload-", suffix);
            file.transferTo(tempFile);

            // Upload bằng file tạm
            Map<?, ?> result = cloudinary.uploader().upload(tempFile, options);
            return toUploadResult(result);

        } catch (IOException e) {
            log.error("Cloudinary upload failed. filename={}, contentType={}",
                    file.getOriginalFilename(), file.getContentType(), e);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        } catch (RuntimeException e) {
            log.error("Cloudinary upload failed unexpectedly. filename={}, contentType={}",
                    file.getOriginalFilename(), file.getContentType(), e);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        } finally {
            // 4. Bắt buộc dọn dẹp: Xóa file tạm sau khi upload xong (dù thành công hay thất
            // bại)
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    log.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }

    public void deleteFile(String publicId, String resourceType) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        if (resourceType == null || resourceType.isBlank()) {
            log.warn("Missing resourceType for publicId={}", publicId);
            throw new AppException(ErrorCode.DELETE_MEDIA_FAILED);
        }

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "invalidate", true));

            String status = stringValue(result.get("result"));

            if (!"ok".equalsIgnoreCase(status) && !"not found".equalsIgnoreCase(status)) {
                log.warn("Cloudinary delete returned unexpected result. publicId={}, result={}",
                        publicId, status);
                throw new AppException(ErrorCode.DELETE_MEDIA_FAILED);
            }

            log.info("Deleted Cloudinary asset. publicId={}, result={}", publicId, status);
        } catch (IOException e) {
            log.error("Cloudinary delete failed. publicId={}", publicId, e);
            throw new AppException(ErrorCode.DELETE_MEDIA_FAILED);
        } catch (RuntimeException e) {
            log.error("Cloudinary delete failed unexpectedly. publicId={}", publicId, e);
            throw new AppException(ErrorCode.DELETE_MEDIA_FAILED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        String contentType = normalizeContentType(file.getContentType());
        boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
        boolean isVideo = ALLOWED_VIDEO_TYPES.contains(contentType);

        if (!isImage && !isVideo) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        long maxSize = isImage ? MAX_IMAGE_BYTES : MAX_VIDEO_BYTES;
        if (file.getSize() > maxSize) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    // private String determineResourceType(String contentType) {
    //     if (contentType.startsWith("video/")) {
    //         return "video";
    //     }
    //     return "image";
    // }

    public MediaType resolveMediaType(String resourceType) {
        if (resourceType == null) {
            return MediaType.IMAGE;
        }

        return switch (resourceType.toLowerCase()) {
            case "video" -> MediaType.VIDEO;
            case "raw" -> MediaType.DOCUMENT;
            default -> MediaType.IMAGE;
        };
    }

    private UploadResult toUploadResult(Map<?, ?> result) {
        String secureUrl = stringValue(result.get("secure_url"));
        String publicId = stringValue(result.get("public_id"));
        String format = stringValue(result.get("format"));
        long bytes = numberValue(result.get("bytes"));

        if (secureUrl == null || publicId == null) {
            log.error("Cloudinary response missing required fields: {}", result);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }

        String resourceType = stringValue(result.get("resource_type"));

        return UploadResult.builder()
                .mediaUrl(secureUrl)
                .publicId(publicId)
                .format(format)
                .resourceType(resourceType)
                .bytes(bytes)
                .build();
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private long numberValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}