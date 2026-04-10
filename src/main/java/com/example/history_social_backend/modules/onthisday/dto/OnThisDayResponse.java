package com.example.history_social_backend.modules.onthisday.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnThisDayResponse {

    private UUID id;
    private LocalDate eventDate;
    private String description;
    private String note;
}