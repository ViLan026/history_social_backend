package com.example.history_social_backend.modules.media.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.media.service.CloudinaryService;
import com.example.history_social_backend.modules.post.dto.internal.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiPaths.MEDIA)
@RequiredArgsConstructor
public class MediaController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadResult> uploadFile(@RequestParam("file") MultipartFile file) {
        UploadResult result = cloudinaryService.uploadFile(file);
        
        return ApiResponse.success(result);
    }

    // @DeleteMapping("/{publicId}")
    // public ApiResponse<Void> deleteFile(@PathVariable String publicId) {
    //     cloudinaryService.deleteFile(publicId);
        
    //     return ApiResponse.success("Delete media successfully");
    // }
}