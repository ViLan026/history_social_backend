package com.example.history_social_backend.modules.post.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.history_social_backend.modules.post.domain.Tag;
import com.example.history_social_backend.modules.post.repository.TagRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Set<Tag> resolveOrCreateTags(Set<String> tagNames) {

        // xử lí tag viết hoa và viết thường đều giống nhau 
        tagNames = tagNames.stream()
                .map(name -> name.trim().toLowerCase())
                .collect(Collectors.toSet());

        // Lấy tag đã tồn tại
        Set<Tag> existingTags = tagRepository.findByNameIn(tagNames);

        Map<String, Tag> tagMap = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        // Tạo tag mới (chưa save ngay)
        List<Tag> newTags = tagNames.stream()
                .filter(name -> !tagMap.containsKey(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .usageCount(0)
                        .build())
                .toList();

        // Save batch
        if (!newTags.isEmpty()) {
            tagRepository.saveAll(newTags);
            newTags.forEach(tag -> tagMap.put(tag.getName(), tag));
        }

        return new HashSet<>(tagMap.values());
    }

    @Transactional
    public void increaseUsageCount(Set<Tag> tags) {
        tags.forEach(tag -> tag.setUsageCount(tag.getUsageCount() + 1));
        // Hibernate auto dirty checking → không cần save()
    }
}