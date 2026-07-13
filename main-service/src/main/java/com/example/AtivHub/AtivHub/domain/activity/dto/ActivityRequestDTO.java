package com.example.AtivHub.AtivHub.domain.activity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActivityRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A recompensa de XP é obrigatória")
        @Min(value = 1, message = "A atividade deve render no mínimo 1 XP")
        Integer xpReward
) {
}