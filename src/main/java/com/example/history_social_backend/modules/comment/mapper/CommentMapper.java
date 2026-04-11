package com.example.history_social_backend.modules.comment.mapper;

import com.example.history_social_backend.modules.comment.domain.Comment;
import com.example.history_social_backend.modules.comment.dto.CommentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponse toResponse(Comment comment);
}