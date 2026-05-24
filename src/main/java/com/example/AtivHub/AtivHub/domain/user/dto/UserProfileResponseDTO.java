package com.example.AtivHub.AtivHub.domain.user.dto;

import com.example.AtivHub.AtivHub.domain.user.User;

import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String name,
        String email,
        String role,
        Integer xp
) {
    public UserProfileResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), user.getXp());
    }
}