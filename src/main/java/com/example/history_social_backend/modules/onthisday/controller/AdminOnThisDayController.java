package com.example.history_social_backend.modules.onthisday.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayRequest;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayResponse;
import com.example.history_social_backend.modules.onthisday.service.OnThisDayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ADMIN_IN_THIS_DAY)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminOnThisDayController {
    private final OnThisDayService service;
    
    @GetMapping("/days")
    public ApiResponse<PageResponse<OnThisDayResponse>> getAll(
            @PageableDefault(size = 20, sort = "eventDate") Pageable pageable) {

        PageResponse<OnThisDayResponse> pageResponse = service.getAll(pageable);
        return ApiResponse.success("Lấy danh sách sự kiện thành công", pageResponse);
    }

    // Admin - Tạo mới sự kiện
    @PostMapping
    public ApiResponse<OnThisDayResponse> create(
            @Valid @RequestBody OnThisDayRequest request) {

        OnThisDayResponse response = service.create(request);
        return ApiResponse.success("Tạo sự kiện lịch sử thành công", response);
    }

    // Admin - Cập nhật sự kiện
    @PutMapping("/{id}")
    public ApiResponse<OnThisDayResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OnThisDayRequest request) {

        OnThisDayResponse response = service.update(id, request);
        return ApiResponse.success("Cập nhật sự kiện thành công", response);
    }

    // Admin - Xóa sự kiện
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("Xóa sự kiện thành công");
    }
}