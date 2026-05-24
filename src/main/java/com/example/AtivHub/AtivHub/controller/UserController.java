package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileResponseDTO;
import com.example.AtivHub.AtivHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile() {
        // Pega o usuário logado a partir do Token JWT
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User studentOrProfessor) {
            return ResponseEntity.ok(new UserProfileResponseDTO(studentOrProfessor));
        }
        return ResponseEntity.status(401).build(); // Não autorizado
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponseDTO>> getGlobalRanking() {
        return ResponseEntity.ok(userService.getGlobalRanking());
    }
}