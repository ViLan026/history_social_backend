package com.example.history_social_backend.modules.post.dto.request;

import com.example.history_social_backend.modules.post.domain.PostStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUpdateRequest {

    @Size(max = 500)
    String title;

    String content;

    // @Size(max = 1000)
    // String summary;

    PostStatus status;

    Set<String> tagNames;

    @Valid
    List<PostSourceRequest> sources;

    /** publicId của các media cần xóa. */
    List<String> removeMediaPublicIds;
}