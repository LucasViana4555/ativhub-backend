package com.example.AtivHub.AtivHub.domain.classroom.dto;

import jakarta.validation.constraints.NotBlank;

public record ClassroomRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório")
        String name,

        @NotBlank(message = "A matéria é obrigatória")
        String subject
) {
}
