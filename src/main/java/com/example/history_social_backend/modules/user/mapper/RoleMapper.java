package com.example.history_social_backend.modules.user.mapper;

import com.example.history_social_backend.modules.user.domain.Role;
import com.example.history_social_backend.modules.user.dto.request.RoleCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.RoleUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    RoleResponse toResponse(Role role);   // dùng trong trường hợp để trả về cho controller hoặc service sau khi đã lấy được một bản ghi từ database    

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(RoleCreationRequest request);  // dùng trong trường hợp để lưu thành một bản ghi mới 


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true) // name is immutable after creation
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Role role, RoleUpdateRequest request);
}

// NullValuePropertyMappingStrategy.IGNORE sẽ bỏ qua các trường null. Nó có hiệu quả trong trường hợp giá trị ở nguồn là null và ở địch thì có giá trị tồn tại 
// với dòng này RoleResponse toResponse(Role role); thì RoleResponse là đích và nếu các trường nào có trong RoleResponse(đích) mà không có trong Role(Nguồn) thì mặc định sẽ là null 