package com.example.history_social_backend.modules.bookmark.mapper;

import com.example.history_social_backend.modules.bookmark.domain.Bookmark;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;

import org.mapstruct.*;
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookmarkMapper {

    @Mapping(source = "id", target = "bookmarkId")
    @Mapping(source = "createdAt", target = "bookmarkedAt")
    @Mapping(target = "post", ignore = true)
    BookmarkResponse toResponse(Bookmark bookmark);
}