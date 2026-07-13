package com.example.gamification.domain.dto;

public record RankingResponseDTO(
        Integer position,
        String name,
        Integer level,
        Integer xp,
        Long answeredActivitiesCount,
        String email,
        String roomCode
) {
}
