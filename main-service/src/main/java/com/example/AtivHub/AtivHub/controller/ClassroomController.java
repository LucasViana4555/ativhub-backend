package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.classroom.dto.ClassroomCreationRequestDTO;
import com.example.AtivHub.AtivHub.domain.classroom.dto.ClassroomResponseDTO;
import com.example.AtivHub.AtivHub.domain.classroom.dto.JoinClassroomRequestDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.service.ClassroomService;
import com.example.AtivHub.AtivHub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ClassroomResponseDTO> createClassroom(
            @RequestBody @Valid ClassroomCreationRequestDTO data,
            @AuthenticationPrincipal User principal) {
        ClassroomResponseDTO classroom = classroomService.createClassroom(data, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(classroom);
    }

    @GetMapping({"/my", "/me"})
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<ClassroomResponseDTO>> getMyClassrooms(@AuthenticationPrincipal User principal) {
        List<ClassroomResponseDTO> classrooms = classroomService.listMyClassrooms(principal);
        return ResponseEntity.ok(classrooms);
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> joinClassroom(
            @RequestBody @Valid JoinClassroomRequestDTO data,
            @AuthenticationPrincipal User principal) {
        classroomService.joinClassroom(data.code(), principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{classroomId}/ranking")
    @PreAuthorize("hasRole('PROFESSOR') or @authService.isStudentInClassroom(authentication, #classroomId)")
    public ResponseEntity<List<RankingResponseDTO>> getClassroomRanking(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(userService.getClassroomRanking(classroomId));
    }

    @DeleteMapping("/{classroomId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteClassroom(
            @PathVariable UUID classroomId,
            @AuthenticationPrincipal User principal) {
        classroomService.deleteClassroom(classroomId, principal);
        return ResponseEntity.noContent().build();
    }
}