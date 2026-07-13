package com.example.AtivHub.AtivHub.domain.activity.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

// Recriando o arquivo para forçar a reindexação pela IDE
public record SubmissionRequestDTO(
        @NotBlank(message = "A resposta não pode ser vazia")
        String answer
) {
}