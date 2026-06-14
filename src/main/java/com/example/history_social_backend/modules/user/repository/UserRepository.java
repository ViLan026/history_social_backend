package com.example.history_social_backend.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.example.history_social_backend.modules.user.domain.AccountStatus;
import com.example.history_social_backend.modules.user.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAllByStatus(AccountStatus status, Pageable pageable);

    // Optional<User> findByUsername(String username);

    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.profile
            LEFT JOIN FETCH u.roles
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN u.profile p
            WHERE (:keyword IS NULL
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id IN :ids")
    List<User> findUsersWithProfileByIds(@Param("ids") Set<UUID> ids);

    @Query("SELECT p.username FROM User u INNER JOIN u.profile p WHERE u.id = :id")
    String findUsernameByUserId(@Param("id") UUID id);

}

// Optional chỉ trả về 1 bản ghi còn bảng join với nó nếu trả về nhiều bản ghi
// thì sẽ được gom lại thành một Collection và trả về List<User> or Set<User>.