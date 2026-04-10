package com.example.history_social_backend.modules.onthisday.mapper;

import com.example.history_social_backend.modules.onthisday.dto.OnThisDayRequest;
import com.example.history_social_backend.modules.onthisday.dto.OnThisDayResponse;
import com.example.history_social_backend.modules.onthisday.domain.OnThisDay;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OnThisDayMapper {

    OnThisDayResponse toResponse(OnThisDay entity);

    List<OnThisDayResponse> toResponseList(List<OnThisDay> entities);

    OnThisDay toEntity(OnThisDayRequest request);

    void updateEntityFromRequest(@MappingTarget OnThisDay entity, OnThisDayRequest request);
}