package com.example.history_social_backend.modules.post.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "fact_check_logs")
public class FactCheckLog extends BaseEntity{

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;

    @Column(name = "knowledge_chunks", columnDefinition = "JSONB")
    String knowledgeChunks;           // lưu JSON string

    @Column(columnDefinition = "TEXT[]")
    String[] contradictions;

    @Column(precision = 4, scale = 2)
    BigDecimal  confidenceScore;

    @Enumerated(EnumType.STRING)
    FactCheckStatus status;

    @Column(columnDefinition = "TEXT")
    String rawResponse;
}