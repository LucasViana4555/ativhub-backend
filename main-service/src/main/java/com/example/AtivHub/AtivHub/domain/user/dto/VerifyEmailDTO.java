package com.example.AtivHub.AtivHub.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailDTO(
        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Código é obrigatório.")
        @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 dígitos.")
        String code
) {}
