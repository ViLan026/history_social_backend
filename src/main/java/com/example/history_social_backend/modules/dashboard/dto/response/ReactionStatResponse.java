package com.example.history_social_backend.modules.dashboard.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionStatResponse {

    private String name;
    private long count;
}