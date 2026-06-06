package com.example.history_social_backend.modules.comment.domain;

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
@Table(name = "comment_hate_speech_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CommentHateSpeechResult extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false, length = 30)
    HateSpeechLabel label;

    @Column(name = "score")
    Double score;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_result", columnDefinition = "jsonb")
    Object rawResult;
}