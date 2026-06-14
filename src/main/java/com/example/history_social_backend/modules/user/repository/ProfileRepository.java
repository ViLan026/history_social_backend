package com.example.history_social_backend.modules.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import com.example.history_social_backend.modules.user.domain.Profile;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    
    boolean existsByUsername(String username);
    
    List<Profile> findAllByUserIdIn(List<UUID> userIds);
}