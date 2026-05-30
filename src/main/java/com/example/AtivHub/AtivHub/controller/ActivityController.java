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

    @PostMapping("/classrooms/{classroomId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @PathVariable UUID classroomId,
            @RequestBody @Valid ActivityRequestDTO data) {
        
        ActivityResponseDTO response = activityService.createActivity(classroomId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/classrooms/{classroomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivityResponseDTO>> listActivitiesByClassroom(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(activityService.listActivitiesByClassroom(classroomId));
    }

    @GetMapping("/{activityId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable UUID activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivityResponseDTO>> listActivities(
            @RequestParam(required = false) UUID classroomId) {
        if (classroomId != null) {
            return ResponseEntity.ok(activityService.listActivitiesByClassroom(classroomId));
        }
        return ResponseEntity.ok(activityService.listAllMyActivities());
    }
}