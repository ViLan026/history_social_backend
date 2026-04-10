package com.example.history_social_backend.modules.comment.mapper;

import com.example.history_social_backend.modules.comment.domain.Comment;
import com.example.history_social_backend.modules.comment.dto.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentResponse toResponse(Comment comment);
}