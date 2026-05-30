package com.example.AtivHub.AtivHub.domain.classroom.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinClassroomRequestDTO(
        @NotBlank(message = "O código da sala não pode ser vazio.")
        String code
) {}
