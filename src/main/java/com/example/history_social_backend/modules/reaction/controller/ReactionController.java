package com.example.history_social_backend.modules.reaction.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.common.response.PageResponse;
import com.example.history_social_backend.modules.reaction.domain.ReactionType;
import com.example.history_social_backend.modules.reaction.dto.request.ReactionRequest;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionDetailResponse;
import com.example.history_social_backend.modules.reaction.dto.response.ReactionStatsResponse;
import com.example.history_social_backend.modules.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.REACTIONS)
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    // bật, tắt, thay đổi reaction
    @PostMapping
    public ApiResponse<Void> toggleReaction(@RequestBody ReactionRequest request) {
        reactionService.toggleReaction(request);
        return ApiResponse.success("Reaction toggled successfully");
    }

    // Lấy số lượng reaction
    @GetMapping("/posts/{postId}/stats")
    public ApiResponse<ReactionStatsResponse> getReactionStats(@PathVariable UUID postId) {
        ReactionStatsResponse stats = reactionService.getReactionStats(postId);
        return ApiResponse.success(stats);
    }

    // GET /api/v1/reactions/posts/{postId}?type=LIKE&page=0&size=20
    @GetMapping("/posts/{postId}")
    public ApiResponse<PageResponse<ReactionDetailResponse>> getReactionDetails(
            @PathVariable UUID postId,
            @RequestParam(required = false) ReactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return ApiResponse.success(reactionService.getReactionDetails(postId, type, page, size));
    }

}