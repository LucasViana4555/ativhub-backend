package com.example.AtivHub.AtivHub.domain.classroom.dto;

import jakarta.validation.constraints.NotBlank;

public record ClassroomCreationRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório")
        String name
) {
}