package com.example.AtivHub.AtivHub.domain.activity.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record SubmissionRequestDTO(
        @NotBlank(message = "A resposta não pode ser vazia")
        String answer
) {
}