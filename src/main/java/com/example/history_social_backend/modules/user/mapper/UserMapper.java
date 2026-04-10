package com.example.history_social_backend.modules.user.mapper;

import com.example.history_social_backend.modules.user.domain.Profile;
import com.example.history_social_backend.modules.user.domain.User;
import com.example.history_social_backend.modules.user.dto.request.UserUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.ProfileResponse;
import com.example.history_social_backend.modules.user.dto.response.UserResponse;
import com.example.history_social_backend.modules.user.dto.response.UserSummaryResponse;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "displayName", source = "profile.displayName")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    UserSummaryResponse toSummaryResponse(User user);

    @Mapping(target = "userId", source = "profile.user.id")
    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user.email", source = "email")
    void updateProfileFromRequest(@MappingTarget Profile profile,
            UserUpdateRequest request);
}