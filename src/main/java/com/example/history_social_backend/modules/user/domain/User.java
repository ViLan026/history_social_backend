package com.example.history_social_backend.modules.user.domain;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    String email;

    @Column(name = "password", length = 255, nullable = false)
    String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", // Tên bảng trung gian sẽ được tự động tạo trong DB
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_roles_user")),
            inverseJoinColumns = @JoinColumn(name = "role_id",  foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    )
    @Builder.Default  // Mặc định là rỗng thay vì null 
    Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    AccountStatus status = AccountStatus.ACTIVE;

    // Bi-directional convenience — cascade so that saving User also saves Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, 
              fetch = FetchType.LAZY, orphanRemoval = true)
    Profile profile;
}


//  FetchType.LAZY ở đây để khi lấy User thì không tự động lấy Profile mà chỉ lấy khi nào gọi getProfile() mới lấy, giúp tiết kiệm tài nguyên khi không cần thiết phải lấy Profile cùng lúc với User.
// CascadeType.ALL để khi thực hiện các thao tác như persist, merge, remove trên User thì cũng sẽ tự động thực hiện tương ứng trên Profile, giúp đồng bộ dữ liệu giữa hai thực thể này. orphanRemoval = true để khi một User bị xóa hoặc không còn tham chiếu đến Profile nữa thì Profile đó cũng sẽ bị xóa khỏi cơ sở dữ liệu, tránh tình trạng dữ liệu rác.
// mappedBy dùng để chỉ ra nơi giữ foreignkey 
// orphanRemaval = true (ở Hibernate)nghĩa là nếu một User bị xóa hoặc không còn tham chiếu đến Profile nữa thì Profile đó cũng sẽ bị xóa khỏi cơ sở dữ liệu, tránh tình trạng dữ liệu rác.
// ON DELETE CASCADE (ở SQL) nghĩa là khi một User bị xóa thì tất cả các Profile liên quan đến User đó cũng sẽ tự động bị xóa khỏi cơ sở dữ liệu, giúp duy trì tính toàn vẹn của dữ liệu và tránh tình trạng dữ liệu rác.

