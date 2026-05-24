package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.activity.Activity;
import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.ActivityRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityResponseDTO createActivity(ActivityRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User professor;

        if (principal instanceof User) {
            professor = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        Activity activity = Activity.builder()
                .title(data.title())
                .description(data.description())
                .xpReward(data.xpReward())
                .professor(professor)
                .build();

        activityRepository.save(activity);
        return new ActivityResponseDTO(activity);
    }

    public List<ActivityResponseDTO> listActivitiesByProfessor() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User professor;

        if (principal instanceof User) {
            professor = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        return activityRepository.findAllByProfessorId(professor.getId())
                .stream()
                .map(ActivityResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<ActivityResponseDTO> listAllActivities() {
        return activityRepository.findAll()
                .stream()
                .map(ActivityResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    public ActivityResponseDTO getActivityById(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));
        return new ActivityResponseDTO(activity);
    }

    public ActivityResponseDTO updateActivity(UUID id, ActivityRequestDTO data) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));

        activity.setTitle(data.title());
        activity.setDescription(data.description());
        activity.setXpReward(data.xpReward());

        activityRepository.save(activity);
        return new ActivityResponseDTO(activity);
    }

    public void deleteActivity(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));
        
        activityRepository.delete(activity);
    }
}