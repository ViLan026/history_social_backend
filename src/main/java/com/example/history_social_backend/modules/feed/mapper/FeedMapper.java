package com.example.history_social_backend.modules.feed.mapper;

import com.example.history_social_backend.modules.feed.dto.FeedPostResponse;
import com.example.history_social_backend.modules.post.domain.Post;
import com.example.history_social_backend.modules.post.domain.PostTag;
import com.example.history_social_backend.modules.post.dto.response.TagResponse;

import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeedMapper {

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "mediaList", target = "mediaList")
    @Mapping(source = "sources", target = "sources")
    @Mapping(source = "postTags", target = "tags", qualifiedByName = "mapPostTagsToTags")
    FeedPostResponse toFeedPostResponse(Post post);

    // === Mapping cho Tags ===
    @Named("mapPostTagsToTags")
    default Set<TagResponse> mapPostTagsToTags(Set<PostTag> postTags) {
        if (postTags == null) {
            return Set.of();
        }
        return postTags.stream()
                .map(this::mapPostTagToTagResponse)
                .collect(Collectors.toSet());
    }

    @Mapping(source = "tag.id", target = "id")
    @Mapping(source = "tag.name", target = "name")
    // Thêm các field khác của Tag nếu có
    TagResponse mapPostTagToTagResponse(PostTag postTag);

}