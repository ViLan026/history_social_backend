package com.example.history_social_backend.modules.post.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_interest_vectors")
public class UserInterestVector extends BaseEntity{

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    // @Column(name = "interest_vector", columnDefinition = "vector(768)", nullable = false)
    // float[] interestVector;

    @Default
    Integer version = 1;
}