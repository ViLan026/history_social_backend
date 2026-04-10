package com.example.history_social_backend.modules.post.dto.request;


import com.example.history_social_backend.modules.post.domain.PostStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 500, message = "Tiêu đề tối đa 500 ký tự")
    String title;

    @NotBlank(message = "Nội dung không được để trống")
    String content;

    PostStatus status = PostStatus.DRAFT;

    Set<String> tagNames;

    @Valid
    List<PostSourceRequest> sources = new ArrayList<>();
}