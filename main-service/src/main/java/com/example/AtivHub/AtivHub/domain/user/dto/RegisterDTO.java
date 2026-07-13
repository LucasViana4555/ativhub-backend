package com.example.AtivHub.AtivHub.domain.user.dto;

import com.example.AtivHub.AtivHub.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String password,

        @NotNull(message = "O perfil (role) é obrigatório")
        Role role
) {
}