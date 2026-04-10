package com.example.history_social_backend.modules.reaction.controller;

import com.example.history_social_backend.common.constant.ApiPaths;
import com.example.history_social_backend.common.response.ApiResponse;
import com.example.history_social_backend.modules.reaction.dto.ReactionRequest;
import com.example.history_social_backend.modules.reaction.dto.ReactionStatsResponse;
import com.example.history_social_backend.modules.reaction.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.REACTIONS)
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    public ApiResponse<Void> toggleReaction(@RequestBody ReactionRequest request) {
        reactionService.toggleReaction(request);
        return ApiResponse.success("Reaction toggled successfully");
    }

    @GetMapping("/posts/{postId}/stats")
    public ApiResponse<ReactionStatsResponse> getReactionStats(@PathVariable UUID postId) {
        ReactionStatsResponse stats = reactionService.getReactionStats(postId);
        return ApiResponse.success(stats);
    }
}