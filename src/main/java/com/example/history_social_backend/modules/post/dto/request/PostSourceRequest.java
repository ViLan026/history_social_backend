package com.example.history_social_backend.modules.post.dto.request;


import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSourceRequest {

    @NotBlank
    String title;

    String url; 
    String authorName;
    Integer publishedYear;
}