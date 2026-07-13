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
        String classroomName,
        LocalDateTime createdAt
) {
    public ActivityResponseDTO(Activity activity) {
        this(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getXpReward(),
                activity.getProfessor().getName(),
                activity.getClassroom() != null ? activity.getClassroom().getName() : "Sem sala",
                activity.getCreatedAt()
        );
    }
}