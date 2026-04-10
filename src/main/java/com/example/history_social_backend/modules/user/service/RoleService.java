package com.example.history_social_backend.modules.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.user.domain.Role;
import com.example.history_social_backend.modules.user.dto.request.RoleCreationRequest;
import com.example.history_social_backend.modules.user.dto.request.RoleUpdateRequest;
import com.example.history_social_backend.modules.user.dto.response.RoleResponse;
import com.example.history_social_backend.modules.user.mapper.RoleMapper;
import com.example.history_social_backend.modules.user.repository.RoleRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()   
                .map(roleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        return roleMapper.toResponse(findRoleById(id));
    }

    @Transactional
    public RoleResponse createRole(RoleCreationRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        Role role = roleMapper.toEntity(request);       // sau khi chạy dòng này thì name và description được set còn các giá trị khác thì null 
        return roleMapper.toResponse(roleRepository.save(role));  // Khi gọi save các giá trị như id, createdAt, updatedAt sẽ được tự động set do đã cấu hình trong domain    
    }

    @Transactional
    public RoleResponse updateRole(UUID id, RoleUpdateRequest request) {
        Role role = findRoleById(id);             // tìm bản ghi 
        roleMapper.updateEntity(role, request);   // set giá trị và trả về void 
        return roleMapper.toResponse(roleRepository.save(role));   // Lưu giá trị vừa được set và trả về dữ liệu cho controller 
    }

    // @Transactional
    // public void deleteRole(UUID id) {
    //     if (!roleRepository.existsById(id)) {
    //         throw new AppException(ErrorCode.ROLE_NOT_FOUND);
    //     }
    //     roleRepository.deleteById(id);
    // }

    public Role findRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

}
