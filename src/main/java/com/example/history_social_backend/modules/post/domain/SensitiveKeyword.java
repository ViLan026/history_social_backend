package com.example.history_social_backend.modules.post.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import com.example.history_social_backend.common.domain.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sensitive_keywords")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensitiveKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer keywordId;

    @Column(unique = true, nullable = false)
    String keyword;

    @Column(nullable = false, length = 30)
    String category;        // nhay_cam, phan_dong, toxic

    @Default
    Integer severity = 1;

    Long createdBy;

}