package com.example.history_social_backend.modules.post.mapper;

import com.example.history_social_backend.modules.post.domain.*;
import com.example.history_social_backend.modules.post.dto.request.PostSourceRequest;
import com.example.history_social_backend.modules.post.dto.response.*;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    @Mapping(source = "postTags", target = "tags")
    PostResponse toPostResponse(Post post);

    @Mapping(source = "postTags", target = "tags")
    @Mapping(source = "mediaList", target = "thumbnailUrl", qualifiedByName = "getThumbnailUrl")
    PostSummaryResponse toSummaryResponse(Post post);

    @Named("getThumbnailUrl")
    default String getThumbnailUrl(List<PostMedia> mediaList) {
        if (mediaList == null) {
            return null;
        }
        return mediaList.stream()
                .filter(m -> m.getMediaType() == MediaType.IMAGE)
                .findFirst()
                .map(PostMedia::getMediaUrl)
                .orElse(null);
    }

    PostMediaResponse toMediaResponse(PostMedia media);

    List<PostMediaResponse> toMediaResponseList(List<PostMedia> list);

    PostSourceResponse toSourceResponse(PostSource source);

    List<PostSourceResponse> toSourceResponseList(List<PostSource> list);

    TagResponse toTagResponse(Tag tag);

    // MapStruct sẽ tự động gọi method này khi map từ Set<PostTag> sang
    // Set<TagResponse>
    default TagResponse postTagToTagResponse(PostTag postTag) {
        if (postTag == null || postTag.getTag() == null) {
            return null;
        }
        return toTagResponse(postTag.getTag());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    PostSource toSourceEntity(PostSourceRequest request);

}
