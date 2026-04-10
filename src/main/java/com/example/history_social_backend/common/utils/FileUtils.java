package com.example.history_social_backend.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

// Dùng cho upload ảnh/video/document

public final class FileUtils {

    private FileUtils() {}

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/mpeg");
    private static final Set<String> DOC_TYPES = Set.of("application/pdf");

    // ================= VALIDATE =================

    public static boolean isImage(MultipartFile file) {
        return file != null && IMAGE_TYPES.contains(file.getContentType());
    }

    public static boolean isVideo(MultipartFile file) {
        return file != null && VIDEO_TYPES.contains(file.getContentType());
    }

    public static boolean isDocument(MultipartFile file) {
        return file != null && DOC_TYPES.contains(file.getContentType());
    }

    // ================= SAVE FILE =================

    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String fileName = UUID.randomUUID() + "." + extension;

        Path path = Paths.get(uploadDir);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path filePath = path.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    // ================= DELETE =================

    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            return false;
        }
    }

    // ================= EXTENSION =================

    public static String getExtension(String filename) {
        if (filename == null) return "";

        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex == -1) return "";

        return filename.substring(lastIndex + 1);
    }

    // ================= SIZE =================

    public static long getFileSize(MultipartFile file) {
        return file != null ? file.getSize() : 0;
    }
}