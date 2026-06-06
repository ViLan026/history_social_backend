package com.example.history_social_backend.modules.report.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostFactCheckClaimResponse {

    UUID id;
    String claimText;
    String label;
    String explanation;
    Object evidence;
    Integer displayOrder;
}