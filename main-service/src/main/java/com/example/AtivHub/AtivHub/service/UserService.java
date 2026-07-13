package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileUpdateRequestDTO;
import com.example.AtivHub.AtivHub.domain.user.event.StudentUpdatedEvent;
import com.example.AtivHub.AtivHub.domain.user.event.StudentDeletedEvent;
import com.example.AtivHub.AtivHub.domain.classroom.event.ClassroomDeletedEvent;
import com.example.AtivHub.AtivHub.infra.exception.*;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import com.example.AtivHub.AtivHub.repository.SubmissionRepository;
import com.example.AtivHub.AtivHub.repository.ActivityRepository;
import com.example.AtivHub.AtivHub.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SubmissionRepository submissionRepository;
    private final ActivityRepository activityRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new StudentNotFoundException("Usuário não encontrado."));
        return new UserProfileResponseDTO(user);
    }

    public List<RankingResponseDTO> getClassroomRanking(UUID classroomId) {
        var topStudentsProjection = userRepository.findTopStudentsByClassroom(classroomId, PageRequest.of(0, 10));

        List<RankingResponseDTO> ranking = new ArrayList<>();
        int position = 1;

        for (var projection : topStudentsProjection) {
            var user = projection.user();
            long submissionsCount = projection.submissionsCount();
            int level = (user.getXp() / 100) + 1;

            String roomCode = null;
            if (org.hibernate.Hibernate.unproxy(user) instanceof Student student) {
                roomCode = student.getClassrooms().stream()
                        .filter(c -> c.getId().equals(classroomId))
                        .map(Classroom::getRoomCode)
                        .findFirst()
                        .orElse(null);
            }

            RankingResponseDTO dto = new RankingResponseDTO(
                    position,
                    user.getName(),
                    level,
                    user.getXp(),
                    submissionsCount,
                    user.getEmail(),
                    roomCode
            );
            ranking.add(dto);
            position++;
        }
        return ranking;
    }

    public List<RankingResponseDTO> getGlobalRanking() {
        var topStudentsProjection = userRepository.findTopStudents(PageRequest.of(0, 100));

        List<RankingResponseDTO> ranking = new ArrayList<>();
        int position = 1;

        for (var projection : topStudentsProjection) {
            var user = projection.user();
            long submissionsCount = projection.submissionsCount();
            int level = (user.getXp() / 100) + 1;

            String roomCode = null;
            if (org.hibernate.Hibernate.unproxy(user) instanceof Student student && !student.getClassrooms().isEmpty()) {
                roomCode = student.getClassrooms().get(0).getRoomCode();
            }

            RankingResponseDTO dto = new RankingResponseDTO(
                    position,
                    user.getName(),
                    level,
                    user.getXp(),
                    submissionsCount,
                    user.getEmail(),
                    roomCode
            );
            ranking.add(dto);
            position++;
        }
        return ranking;
    }

    @Transactional
    public UserProfileResponseDTO updateProfile(UUID userId, UserProfileUpdateRequestDTO data, PasswordEncoder passwordEncoder) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new StudentNotFoundException("Usuário não encontrado."));

        if (data.name() != null && !data.name().trim().isEmpty()) {
            user.setName(data.name());
        }

        if (data.password() != null && !data.password().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(data.password()));
        }

        user.setPhotoUrl(data.photoUrl());

        if (org.hibernate.Hibernate.unproxy(user) instanceof Professor professor) {
            if (data.schoolName() != null && !data.schoolName().trim().isEmpty()) {
                professor.setSchoolName(data.schoolName());
            }
        } else if (org.hibernate.Hibernate.unproxy(user) instanceof Student student) {
            if (data.schoolName() != null && !data.schoolName().trim().isEmpty()) {
                student.setSchoolName(data.schoolName());
            }
            
            // avisa se o nome mudou pra sincronizar a gamificacao
            if (data.name() != null && !data.name().trim().isEmpty()) {
                try {
                    eventPublisher.publishEvent(new StudentUpdatedEvent(student.getId(), student.getName()));
                } catch (Exception e) {
                    System.err.println("Erro ao publicar evento de atualização do estudante: " + e.getMessage());
                }
            }
        }

        var savedUser = userRepository.save(user);
        return new UserProfileResponseDTO(savedUser);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new StudentNotFoundException("Usuário não encontrado."));

        // deleta token de email se existir
        emailVerificationTokenRepository.deleteByUser(user);

        if (org.hibernate.Hibernate.unproxy(user) instanceof Student student) {
            // remove submissoes
            var submissions = submissionRepository.findAllByStudentId(student.getId());
            submissionRepository.deleteAll(submissions);

            // avisa o servico de gamificacao
            try {
                eventPublisher.publishEvent(new StudentDeletedEvent(student.getId()));
            } catch (Exception e) {
                System.err.println("Erro ao publicar StudentDeletedEvent: " + e.getMessage());
            }

            // limpa salas pra atualizar a tabela de vinculo
            student.getClassrooms().clear();
            userRepository.save(student);

            userRepository.delete(student);

        } else if (org.hibernate.Hibernate.unproxy(user) instanceof Professor professor) {
            // busca as ativs do prof
            var activities = activityRepository.findAllByProfessorId(professor.getId());
            
            // deleta submissoes das ativs
            for (var activity : activities) {
                var submissions = submissionRepository.findAllByActivityId(activity.getId());
                submissionRepository.deleteAll(submissions);
            }
            // deleta as ativs
            activityRepository.deleteAll(activities);

            // avisa o servico de gamificacao pra cada sala
            if (professor.getClassrooms() != null) {
                for (var classroom : professor.getClassrooms()) {
                    try {
                        eventPublisher.publishEvent(new ClassroomDeletedEvent(classroom.getId()));
                    } catch (Exception e) {
                        System.err.println("Erro ao publicar ClassroomDeletedEvent para sala: " + classroom.getId() + " - " + e.getMessage());
                    }
                }
            }

            // deleta o prof (vai em cascata pras salas)
            userRepository.delete(professor);
        }
    }
}