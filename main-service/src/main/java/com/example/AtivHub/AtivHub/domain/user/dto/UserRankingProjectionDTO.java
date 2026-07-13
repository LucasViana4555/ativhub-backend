package com.example.AtivHub.AtivHub.domain.user.dto;

import com.example.AtivHub.AtivHub.domain.user.User;

public record UserRankingProjectionDTO(
        User user,
        Long submissionsCount
) {
}