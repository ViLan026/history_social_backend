package com.example.history_social_backend.modules.onthisday.service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayRequest;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayResponse;
import com.example.history_social_backend.modules.onthisday.domain.OnThisDay;
import com.example.history_social_backend.modules.onthisday.mapper.OnThisDayMapper;
import com.example.history_social_backend.modules.onthisday.repository.OnThisDayRepository;
import com.example.history_social_backend.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnThisDayService {

    private final OnThisDayRepository repository;
    private final OnThisDayMapper mapper;

    // Lấy tháng/ngày từ server time (LocalDate.now())
    public List<OnThisDayResponse> getTodayEvents() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        List<OnThisDay> events = repository.findEventsByMonthAndDay(month, day);
        return mapper.toResponseList(events);
    }

    // Admin - Tạo mới sự kiện
    @Transactional
    public OnThisDayResponse create(OnThisDayRequest request) {
        OnThisDay entity = mapper.toEntity(request);
        OnThisDay saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    // Admin - Cập nhật sự kiện
    @Transactional
    public OnThisDayResponse update(UUID id, OnThisDayRequest request) {
        OnThisDay entity = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ON_THIS_DAY_NOT_FOUND));

        mapper.updateEntityFromRequest(entity, request);
        OnThisDay saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    // Admin - Xóa sự kiện
    @Transactional
    public void delete(UUID id) {
        OnThisDay entity = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ON_THIS_DAY_NOT_FOUND));

        repository.delete(entity);
    }

    // Admin - Lấy danh sách toàn bộ (có phân trang)
    public PageResponse<OnThisDayResponse> getAll(Pageable pageable) {
        
        Page<OnThisDay> entityPage = repository.findAll(pageable);
        Page<OnThisDayResponse> responsePage = entityPage.map(mapper::toResponse);
        return PageResponse.from(responsePage);
    }
}