package com.example.history_social_backend.modules.bookmark.mapper;

import com.example.history_social_backend.modules.bookmark.domain.Bookmark;
import com.example.history_social_backend.modules.bookmark.dto.response.BookmarkResponse;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.user.domain.User;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookmarkMapper {

    BookmarkResponse toResponse(Bookmark bookmark);

    BookmarkResponse.PostInfo toPostInfo(Post post);

    BookmarkResponse.AuthorInfo toAuthorInfo(User user);

 
}