package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User user) {
            return ResponseEntity.ok(userService.getUserProfile(user.getId()));
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