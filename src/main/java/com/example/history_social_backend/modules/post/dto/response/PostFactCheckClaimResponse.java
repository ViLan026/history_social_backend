package com.example.history_social_backend.modules.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class PostFactCheckClaimResponse {

    UUID id;
    String claimText;
    String label;
    String explanation;
    Object evidence;
    Integer displayOrder;
}