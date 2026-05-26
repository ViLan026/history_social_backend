package com.example.history_social_backend.modules.onthisday.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayResponse;
import com.example.history_social_backend.modules.onthisday.service.OnThisDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ONTHISDAY)
@RequiredArgsConstructor
public class OnThisDayController {

    private final OnThisDayService service;

    // GET /api/onthisday/today
    @GetMapping("/today")
    public ApiResponse<List<OnThisDayResponse>> getTodayEvents() {
        List<OnThisDayResponse> data = service.getTodayEvents();
        return ApiResponse.success("Lấy sự kiện ngày này năm xưa thành công", data);
    }

}