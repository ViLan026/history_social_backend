package com.example.history_social_backend.modules.onthisday.controller;

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

import java.util.List;
import java.util.UUID;

/**
 * Controller cho module OnThisDay (FR-13 & FR-14)
 * - Public route: /api/onthisday/today (không cần auth)
 * - Admin routes: /api/admin/onthisday/* (yêu cầu ROLE_ADMIN)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnThisDayController {

    private final OnThisDayService service;

    // ====================== PUBLIC API (FR-13) ======================

    /**
     * FR-13: Hiển thị sự kiện "Ngày này năm xưa" (public)
     * Route: GET /api/onthisday/today
     */
    @GetMapping("/onthisday/today")
    public ApiResponse<List<OnThisDayResponse>> getTodayEvents() {
        List<OnThisDayResponse> data = service.getTodayEvents();
        return ApiResponse.success("Lấy sự kiện ngày này năm xưa thành công", data);
    }

    // ====================== ADMIN API (FR-14) ======================

    /**
     * FR-14: Admin - Lấy danh sách toàn bộ sự kiện (phân trang)
     * Route: GET /api/admin/onthisday
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/onthisday")
    public ApiResponse<PageResponse<OnThisDayResponse>> getAll(
            @PageableDefault(size = 20, sort = "eventDate") Pageable pageable) {

        PageResponse<OnThisDayResponse> pageResponse = service.getAll(pageable);
        return ApiResponse.success("Lấy danh sách sự kiện thành công", pageResponse);
    }

    /**
     * FR-14: Admin - Tạo mới sự kiện
     * Route: POST /api/admin/onthisday
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/onthisday")
    public ApiResponse<OnThisDayResponse> create(
            @Valid @RequestBody OnThisDayRequest request) {

        OnThisDayResponse response = service.create(request);
        return ApiResponse.success("Tạo sự kiện lịch sử thành công", response);
    }

    /**
     * FR-14: Admin - Cập nhật sự kiện
     * Route: PUT /api/admin/onthisday/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/onthisday/{id}")
    public ApiResponse<OnThisDayResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OnThisDayRequest request) {

        OnThisDayResponse response = service.update(id, request);
        return ApiResponse.success("Cập nhật sự kiện thành công", response);
    }

    /**
     * FR-14: Admin - Xóa sự kiện
     * Route: DELETE /api/admin/onthisday/{id}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/onthisday/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("Xóa sự kiện thành công");
    }
}