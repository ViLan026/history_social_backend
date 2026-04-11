package com.example.history_social_backend.modules.bookmark.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookmarkToggleResponse {
    String action;
    boolean bookmarked;
    String message;
}