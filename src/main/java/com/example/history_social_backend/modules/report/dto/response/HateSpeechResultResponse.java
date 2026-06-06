package com.example.history_social_backend.modules.report.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HateSpeechResultResponse {

    String label;
    Double score;
}