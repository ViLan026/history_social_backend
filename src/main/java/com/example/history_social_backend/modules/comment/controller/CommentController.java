package com.example.history_social_backend.modules.comment.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.comment.dto.CommentRequest;
import com.example.history_social_backend.modules.comment.dto.CommentResponse;
import com.example.history_social_backend.modules.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.COMMENTS)
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return commentService.createComment(request);
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<PageResponse<CommentResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(commentService.getCommentsByPostId(postId, page, size));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable UUID commentId) {
        return ApiResponse.success(commentService.deleteComment(commentId));
    }
}