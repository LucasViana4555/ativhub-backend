package com.example.gamification.controller;

import com.example.gamification.domain.Classroom;
import com.example.gamification.domain.dto.RankingResponseDTO;
import com.example.gamification.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RankingController {

    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<RankingResponseDTO>> getRanking(
            @RequestParam(required = false) UUID classroomId) {
        
        List<RankingResponseDTO> ranking = new ArrayList<>();
        int position = 1;

        if (classroomId != null) {
            var students = studentRepository.findTopStudentsByClassroom(classroomId, PageRequest.of(0, 10));
            for (var student : students) {
                String roomCode = student.getClassrooms().stream()
                        .filter(c -> c.getId().equals(classroomId))
                        .map(Classroom::getRoomCode)
                        .findFirst()
                        .orElse(null);

                ranking.add(new RankingResponseDTO(
                        position,
                        student.getName(),
                        student.getLevel(),
                        student.getXp(),
                        student.getSubmissionsCount().longValue(),
                        student.getEmail(),
                        roomCode
                ));
                position++;
            }
        } else {
            var students = studentRepository.findTopStudents(PageRequest.of(0, 100));
            for (var student : students) {
                String roomCode = student.getClassrooms().isEmpty() ? null : student.getClassrooms().get(0).getRoomCode();
                
                ranking.add(new RankingResponseDTO(
                        position,
                        student.getName(),
                        student.getLevel(),
                        student.getXp(),
                        student.getSubmissionsCount().longValue(),
                        student.getEmail(),
                        roomCode
                ));
                position++;
            }
        }

        return ResponseEntity.ok(ranking);
    }
}
