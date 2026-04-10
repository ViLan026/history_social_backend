package com.example.history_social_backend.modules.onthisday.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnThisDayRequest {

    @NotNull(message = "Ngày sự kiện không được để trống")
    private LocalDate eventDate;

    @NotBlank(message = "Mô tả sự kiện không được để trống")
    private String description;

    private String note;
}