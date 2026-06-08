package com.example.history_social_backend.modules.post.domain;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

@Entity
@Table(name = "post_fact_check_claims")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class PostFactCheckClaim extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    Post post;

    @Column(name = "claim_text", nullable = false, columnDefinition = "text")
    String claimText;

    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false, length = 30)
    FactCheckLabel label;

    @Column(name = "explanation", columnDefinition = "text")
    String explanation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence", columnDefinition = "jsonb")
    Object evidence;

    @Column(name = "display_order", nullable = false)
    Integer displayOrder;
}





