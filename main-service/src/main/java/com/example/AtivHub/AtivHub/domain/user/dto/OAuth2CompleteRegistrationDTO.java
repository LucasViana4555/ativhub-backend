package com.example.AtivHub.AtivHub.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuth2CompleteRegistrationDTO(
    @NotBlank(message = "Token temporário é obrigatório.") String tempToken,
    @NotBlank(message = "Nome é obrigatório.") String name,
    @NotBlank(message = "Email é obrigatório.") String email,
    @NotBlank(message = "Perfil (role) é obrigatório.") String role, // "PROFESSOR" ou "STUDENT"/"ALUNO"
    String schoolName,    // Requerido se role for PROFESSOR
    String subject,       // Requerido se role for PROFESSOR
    String roomCode       // Opcional/Requerido se role for STUDENT
) {}
