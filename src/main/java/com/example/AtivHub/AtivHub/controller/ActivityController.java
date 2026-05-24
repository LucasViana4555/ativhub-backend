package com.example.AtivHub.AtivHub.controller;

import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityResponseDTO;
import com.example.AtivHub.AtivHub.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ActivityResponseDTO> createActivity(@RequestBody @Valid ActivityRequestDTO data) {
        ActivityResponseDTO response = activityService.createActivity(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> listAllActivities() {
        return ResponseEntity.ok(activityService.listAllActivities());
    }

    @GetMapping("/professor")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<ActivityResponseDTO>> listActivitiesByProfessor() {
        return ResponseEntity.ok(activityService.listActivitiesByProfessor());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable UUID id, 
            @RequestBody @Valid ActivityRequestDTO data) {
        ActivityResponseDTO response = activityService.updateActivity(id, data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}