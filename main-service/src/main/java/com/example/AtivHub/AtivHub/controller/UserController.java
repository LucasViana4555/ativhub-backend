package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileUpdateRequestDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gerenciamento e consulta de usuários")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @Operation(summary = "Obtém o perfil do usuário logado", description = "Retorna os detalhes do perfil do usuário autenticado (aluno ou professor)")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User user) {
            return ResponseEntity.ok(userService.getUserProfile(user.getId()));
        }
        return ResponseEntity.status(401).build();
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza as configurações do perfil", description = "Permite alterar nome, senha, foto e instituição de estudo/trabalho do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso", 
                     content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    })
    public ResponseEntity<UserProfileResponseDTO> updateMyProfile(@RequestBody @Valid UserProfileUpdateRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User user) {
            return ResponseEntity.ok(userService.updateProfile(user.getId(), data, passwordEncoder));
        }
        return ResponseEntity.status(401).build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "Exclui permanentemente a conta logada", description = "Apaga o perfil do usuário logado (aluno ou professor), todas as salas criadas, atividades e submissões vinculadas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Void> deleteMyAccount() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User user) {
            userService.deleteUser(user.getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponseDTO>> getRanking(
            @RequestParam(required = false) UUID classroomId) {
        if (classroomId != null) {
            return ResponseEntity.ok(userService.getClassroomRanking(classroomId));
        }
        return ResponseEntity.ok(userService.getGlobalRanking());
    }
}