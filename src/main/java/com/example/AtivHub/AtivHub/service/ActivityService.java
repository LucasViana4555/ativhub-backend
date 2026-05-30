package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.activity.Activity;
import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.ActivityResponseDTO;
import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.ActivityRepository;
import com.example.AtivHub.AtivHub.repository.ClassroomRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    public ActivityResponseDTO createActivity(UUID classroomId, ActivityRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Professor professor)) {
            throw new AccessDeniedException("Apenas professores podem criar atividades.");
        }

        User userObj = userRepository.findById(professor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
        Professor managedProfessor = (Professor) org.hibernate.Hibernate.unproxy(userObj);

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Sala de aula não encontrada."));

        if (!classroom.getTeacher().getId().equals(managedProfessor.getId())) {
            throw new AccessDeniedException("Você não tem permissão para criar atividades nesta sala.");
        }

        Activity activity = Activity.builder()
                .title(data.title())
                .description(data.description())
                .xpReward(data.xpReward())
                .professor(managedProfessor)
                .classroom(classroom)
                .build();

        activityRepository.save(activity);
        return new ActivityResponseDTO(activity);
    }

    public List<ActivityResponseDTO> listActivitiesByClassroom(UUID classroomId) {
        var principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Sala de aula não encontrada."));

        boolean isProfessorOfClass = classroom.getTeacher().getId().equals(principal.getId());
        boolean isStudentInClass = classroom.getStudents().stream().anyMatch(s -> s.getId().equals(principal.getId()));

        if (!isProfessorOfClass && !isStudentInClass) {
            throw new AccessDeniedException("Você não tem permissão para visualizar as atividades desta sala.");
        }

        return activityRepository.findAllByClassroomId(classroomId)
                .stream()
                .map(ActivityResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ActivityResponseDTO getActivityById(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));
        
        // Validação de segurança pode ser adicionada aqui se necessário
        
        return new ActivityResponseDTO(activity);
    }

    public List<ActivityResponseDTO> listAllMyActivities() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);

        if (unproxied instanceof Professor professor) {
            return activityRepository.findAllByProfessorId(professor.getId())
                    .stream()
                    .map(ActivityResponseDTO::new)
                    .collect(Collectors.toList());
        } else if (unproxied instanceof Student student) {
            User userObj = userRepository.findById(student.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado."));
            Student managedStudent = (Student) org.hibernate.Hibernate.unproxy(userObj);

            if (managedStudent.getClassrooms().isEmpty()) {
                return List.of();
            }
            List<UUID> classroomIds = managedStudent.getClassrooms().stream()
                    .map(com.example.AtivHub.AtivHub.domain.classroom.Classroom::getId)
                    .collect(Collectors.toList());
            return activityRepository.findAllByClassroomIdIn(classroomIds)
                    .stream()
                    .map(ActivityResponseDTO::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}