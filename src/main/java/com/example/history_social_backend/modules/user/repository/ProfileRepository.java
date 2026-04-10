package com.example.history_social_backend.modules.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.user.domain.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    
    boolean existsByUsername(String username);
}