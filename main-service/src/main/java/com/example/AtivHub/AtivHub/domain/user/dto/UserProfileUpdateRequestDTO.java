package com.example.AtivHub.AtivHub.domain.user.dto;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequestDTO(
        String name,
        
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,
        
        String photoUrl,
        
        String schoolName
) {}
