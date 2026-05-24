package com.example.AtivHub.AtivHub.domain.user.dto;

public record RankingResponseDTO(
        Integer position,
        String name,
        String email,
        Integer level,
        Integer xp,
        Long answeredActivitiesCount
) {
}
