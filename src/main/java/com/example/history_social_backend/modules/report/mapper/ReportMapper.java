package com.example.history_social_backend.modules.report.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.history_social_backend.modules.report.domain.Report;
import com.example.history_social_backend.modules.report.dto.response.ReportResponse;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReportMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    ReportResponse toResponse(Report report);
}