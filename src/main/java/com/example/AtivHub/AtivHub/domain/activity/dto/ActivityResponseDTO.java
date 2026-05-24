package com.example.AtivHub.AtivHub.domain.activity.dto;

import com.example.AtivHub.AtivHub.domain.activity.Activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponseDTO(
        UUID id,
        String title,
        String description,
        Integer xpReward,
        String professorName,
        LocalDateTime createdAt
) {
    public ActivityResponseDTO(Activity activity) {
        this(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getXpReward(),
                activity.getProfessor().getName(),
                activity.getCreatedAt()
        );
    }
}