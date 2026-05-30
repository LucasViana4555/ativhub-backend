package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.dto.UserProfileResponseDTO;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuário não encontrado."));
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
}