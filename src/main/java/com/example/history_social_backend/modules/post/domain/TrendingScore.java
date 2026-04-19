package com.example.history_social_backend.modules.post.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "trending_scores")
public class TrendingScore extends BaseEntity{

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(precision = 5, scale = 4)
    BigDecimal wilsonScore;

    @Column(precision = 8, scale = 2)
    BigDecimal engagementScore;

    @Column(precision = 8, scale = 2)
    BigDecimal finalScore;

    String period;               // daily, peak_hour
}