package com.example.history_social_backend.modules.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.media.internal.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.history_social_backend.modules.post.domain.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Ngưỡng để quyết định dùng chunked upload (50MB)
    private static final long CHUNKED_UPLOAD_THRESHOLD = 50 * 1024 * 1024;

    // Giới hạn file tối đa (100MB)
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    @SuppressWarnings("unchecked")
    public UploadResult uploadFile(MultipartFile file, String folderName) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.MEDIA_FILE_EMPTY);
        }

        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.MEDIA_FILE_TOO_LARGE);
        }

        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", folderName);
            uploadParams.put("resource_type", "auto"); // Auto-detect: Cloudinary tự nhận diện image/video
            uploadParams.put("use_filename", true);
            uploadParams.put("unique_filename", true);

            uploadParams.put("timeout", 30000);

            Map<String, Object> uploadResult;

            // CHUNKED UPLOAD cho file lớn (>50MB)
            if (fileSize > CHUNKED_UPLOAD_THRESHOLD) {
                log.info("File lớn ({} bytes), sử dụng chunked upload", fileSize);
                uploadParams.put("chunk_size", 6_000_000); // Chunk 6MB mỗi lần

                // Upload trực tiếp từ InputStream, không qua File
                uploadResult = (Map<String, Object>) cloudinary.uploader().uploadLarge(
                        file.getInputStream(),
                        uploadParams);
            } else {
                // UPLOAD THƯỜNG cho file nhỏ
                // Đọc trực tiếp từ byte array (RAM), không tạo File tạm
                uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                        file.getBytes(),
                        uploadParams);
            }

            // Parse kết quả từ Cloudinary
            return UploadResult.builder()
                    .mediaUrl((String) uploadResult.get("secure_url"))
                    .publicId((String) uploadResult.get("public_id"))
                    .format((String) uploadResult.get("format"))
                    .bytes(((Number) uploadResult.get("bytes")).longValue())
                    .resourceType((String) uploadResult.get("resource_type"))
                    .build();

        } catch (IOException e) {
            log.error("Lỗi khi upload file lên Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.MEDIA_UPLOAD_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteFile(String publicId, String resourceType) {
        // Validate publicId
        if (publicId == null || publicId.isBlank()) {
            throw new AppException(ErrorCode.MEDIA_INVALID_PUBLIC_ID);
        }

        try {
            // Dùng HashMap thuần thay cho ObjectUtils.asMap để đảm bảo an toàn kiểu
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("resource_type", resourceType != null ? resourceType : "image");

            // Ép kiểu kết quả trả về từ Cloudinary
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().destroy(publicId, deleteParams);

            String resultStatus = (String) result.get("result");
            if ("ok".equals(resultStatus)) {
                log.info("Xóa media thành công. PublicId: {}", publicId);
            } else if ("not found".equals(resultStatus)) {
                log.warn("Cloudinary báo không tìm thấy (not found) publicId: {}", publicId);
            } else {
                log.warn("Xóa media không thành công. PublicId: {}, Result: {}", publicId, resultStatus);
                throw new AppException(ErrorCode.MEDIA_DELETE_FAILED);
            }

        } catch (IOException e) {
            log.error("Lỗi khi xóa media từ Cloudinary: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.MEDIA_DELETE_FAILED);
        }
    }

    public void deleteFiles(List<String> publicIds, String resourceType) throws Exception {
        if (publicIds == null || publicIds.isEmpty())
            return;

        try {
            // Hàm deleteResources() thuộc Admin API. Hỗ trợ xóa tối đa 100 ID mỗi lần gọi.
            // Map<String, String> options = ObjectUtils.asMap("resource_type",
            // resourceType);
            // cloudinary.api().deleteResources(publicIds, options);

            cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("resource_type", resourceType));
            log.info("Đã batch-delete {} files trên Cloudinary", publicIds.size());

        } catch (Exception e) {
            log.error("Lỗi khi batch-delete danh sách file trên Cloudinary: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteFolder(String folderPath) throws Exception {
        try {
            // Bước 1: BẮT BUỘC phải xóa sạch các file bên trong thư mục trước.
            // Nếu thư mục còn chứa file, hàm deleteFolder ở bước 2 sẽ báo lỗi.
            cloudinary.api().deleteResourcesByPrefix(folderPath, ObjectUtils.emptyMap());

            // Bước 2: Xóa cái vỏ thư mục (lúc này đã rỗng)
            cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());
            log.info("Đã xóa toàn bộ thư mục trên Cloudinary: {}", folderPath);
        } catch (Exception e) {
            log.error("Lỗi khi xóa thư mục {} trên Cloudinary: {}", folderPath, e.getMessage());
            throw e;
        }
    }

    // Map Cloudinary resource type sang MediaType enum
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
}