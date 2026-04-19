package com.example.history_social_backend.modules.onthisday.domain;

import java.time.LocalDate;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;
import com.example.history_social_backend.common.utils.UuidV7;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "on_this_day")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OnThisDay extends BaseEntity {

    @Id
    @UuidV7
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    UUID id;

    @Column(nullable = false, unique = true)
    LocalDate eventDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String note;
}