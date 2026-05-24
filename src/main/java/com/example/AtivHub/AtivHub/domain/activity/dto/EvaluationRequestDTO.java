package com.example.AtivHub.AtivHub.domain.activity.dto;

import jakarta.validation.constraints.NotNull;

public record EvaluationRequestDTO(
        String feedback,

        @NotNull(message = "A nota (grade) é obrigatória")
        Integer grade,

        @NotNull(message = "A indicação de aprovação é obrigatória")
        Boolean approved
) {
}