package com.example.history_social_backend.modules.onthisday.domain;

import java.time.LocalDate;
import java.util.UUID;

import com.example.history_social_backend.common.domain.BaseEntity;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    UUID id;

    @Column(nullable = false, unique = true)
    LocalDate eventDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String note;
}