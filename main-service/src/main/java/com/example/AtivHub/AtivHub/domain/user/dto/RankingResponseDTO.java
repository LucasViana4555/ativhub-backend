package com.example.AtivHub.AtivHub.domain.user.dto;

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