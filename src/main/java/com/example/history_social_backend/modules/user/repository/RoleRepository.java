package com.example.history_social_backend.modules.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.history_social_backend.modules.user.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

}

// Optional ở đây để trách trường hợp không trả về Role nào sẽ trả về rỗng chứ không phải null để tránh NullPointerException.
// giá trị rỗng nghĩa là đối tượng đó đã được khởi tạo trong RAM rồi nhưng giá trị của nó là trống
// còn null nghĩa là đối tượng chưa được khơi tạo trọng RAM và nếu như cố gắng truy cập thì sẽ hiển thị ra lỗi NullPointerException.