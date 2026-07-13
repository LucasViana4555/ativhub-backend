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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void createActivity_Success() {
        // arrange
        UUID professorId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        Professor professor = new Professor("Professor Xavier", "xavier@school.com", "pass", "Muted", "Mutantes");
        professor.setId(professorId);

        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Sala de Mutantes")
                .roomCode("MUT-001")
                .teacher(professor)
                .build();

        ActivityRequestDTO request = new ActivityRequestDTO("Controlar Poderes", "Aprenda a controlar o magnetismo", 50);

        when(userRepository.findById(professorId)).thenReturn(Optional.of(professor));
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

        // act
        ActivityResponseDTO result = activityService.createActivity(classroomId, request, professor);

        // assert
        assertNotNull(result);
        assertEquals(request.title(), result.title());
        assertEquals(request.xpReward(), result.xpReward());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void createActivity_ThrowsAccessDenied_WhenUserIsStudent() {
        // arrange
        UUID studentId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        Student student = new Student("Wolverine", "logan@xmen.com", "pass");
        student.setId(studentId);

        ActivityRequestDTO request = new ActivityRequestDTO("Combate Corpo a Corpo", "Prática com garras", 20);

        // act e assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            activityService.createActivity(classroomId, request, student);
        });

        assertEquals("Apenas professores podem criar atividades.", exception.getMessage());
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void createActivity_ThrowsAccessDenied_WhenProfessorDoesNotTeachClass() {
        // arrange
        UUID professorId = UUID.randomUUID();
        UUID otherProfessorId = UUID.randomUUID();
        UUID classroomId = UUID.randomUUID();

        Professor professor = new Professor("Professor Xavier", "xavier@school.com", "pass", "Muted", "Mutantes");
        professor.setId(professorId);

        Professor otherProfessor = new Professor("Magneto", "magneto@school.com", "pass", "Irmandade", "Metal");
        otherProfessor.setId(otherProfessorId);

        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Sala de Metais")
                .roomCode("MET-001")
                .teacher(otherProfessor) // outro prof da aula
                .build();

        ActivityRequestDTO request = new ActivityRequestDTO("Levitar Pontes", "Prática de magnetismo", 100);

        when(userRepository.findById(professorId)).thenReturn(Optional.of(professor));
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

        // act e assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            activityService.createActivity(classroomId, request, professor);
        });

        assertEquals("Você não tem permissão para criar atividades nesta sala.", exception.getMessage());
        verify(activityRepository, never()).save(any(Activity.class));
    }
}
