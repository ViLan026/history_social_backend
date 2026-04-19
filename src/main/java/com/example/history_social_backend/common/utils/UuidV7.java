package com.example.history_social_backend.common.utils;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IdGeneratorType(UuidV7Generator.class) // Trỏ thẳng đến class generator
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UuidV7 {
    // Annotation đánh dấu cho Hibernate biết field này sẽ dùng UuidV7Generator
}