package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.activity.dto.EvaluationRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionResponseDTO;
import com.example.AtivHub.AtivHub.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Recriando o arquivo para forçar a reindexação pela IDE
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/activities/{activityId}/submit")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<SubmissionResponseDTO> submitActivity(
            @PathVariable UUID activityId,
            @RequestBody @Valid SubmissionRequestDTO data) {
        
        SubmissionResponseDTO response = submissionService.submitActivity(activityId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/submissions/{submissionId}/feedback")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<SubmissionResponseDTO> evaluateSubmission(
            @PathVariable UUID submissionId,
            @RequestBody @Valid EvaluationRequestDTO data) {
        
        SubmissionResponseDTO response = submissionService.evaluateSubmission(submissionId, data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activities/{activityId}/submissions")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<SubmissionResponseDTO>> listSubmissionsByActivity(@PathVariable UUID activityId) {
        return ResponseEntity.ok(submissionService.listSubmissionsByActivity(activityId));
    }

    @GetMapping("/submissions/me")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<SubmissionResponseDTO>> listMySubmissions() {
        return ResponseEntity.ok(submissionService.listMySubmissions());
    }
}