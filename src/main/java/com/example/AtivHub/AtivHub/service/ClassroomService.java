package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import com.example.AtivHub.AtivHub.domain.classroom.dto.ClassroomCreationRequestDTO;
import com.example.AtivHub.AtivHub.domain.classroom.dto.ClassroomResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.ClassroomRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private static final SecureRandom random = new SecureRandom();

    public ClassroomResponseDTO createClassroom(ClassroomCreationRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Professor professor)) {
            throw new IllegalStateException("Apenas professores podem criar salas.");
        }

        User userObj = userRepository.findById(professor.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Professor não encontrado."));
        Professor managedProfessor = (Professor) org.hibernate.Hibernate.unproxy(userObj);

        String roomCode = generateUniqueRoomCode(managedProfessor.getSubject());

        Classroom classroom = Classroom.builder()
                .name(data.name())
                .teacher(managedProfessor)
                .roomCode(roomCode)
                .build();

        classroomRepository.save(classroom);
        return new ClassroomResponseDTO(classroom);
    }

    public List<ClassroomResponseDTO> listMyClassrooms() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Professor professor)) {
            throw new IllegalStateException("Usuário não é um professor.");
        }

        return classroomRepository.findByTeacherId(professor.getId()).stream()
                .map(ClassroomResponseDTO::new)
                .collect(Collectors.toList());
    }

    private String generateUniqueRoomCode(String subject) {
        String cleanSubject = "";
        if (subject != null) {
            String temp = java.text.Normalizer.normalize(subject, java.text.Normalizer.Form.NFD);
            cleanSubject = temp.replaceAll("\\p{M}", "").replaceAll("[^a-zA-Z]", "").toUpperCase();
        }
        String prefix = cleanSubject.length() >= 3 ? cleanSubject.substring(0, 3) : cleanSubject;
        if (prefix.isEmpty()) {
            prefix = "ROOM";
        }

        String code;
        do {
            int num = random.nextInt(1000);
            code = String.format("%s-%03d", prefix, num);
        } while (classroomRepository.existsByRoomCode(code));
        return code;
    }

    public void deleteClassroom(UUID id) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Professor professor)) {
            throw new IllegalStateException("Apenas professores podem excluir salas.");
        }

        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Sala de aula não encontrada."));

        if (!classroom.getTeacher().getId().equals(professor.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Você não tem permissão para excluir esta sala.");
        }

        // Dissociar os alunos desta sala antes de deletar
        if (classroom.getStudents() != null) {
            for (Student student : classroom.getStudents()) {
                student.getClassrooms().remove(classroom);
            }
        }

        classroomRepository.delete(classroom);
    }

    public void joinClassroom(String code) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Student student)) {
            throw new IllegalStateException("Apenas alunos podem entrar em salas.");
        }

        User userObj = userRepository.findById(student.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Aluno não encontrado."));
        Student managedStudent = (Student) org.hibernate.Hibernate.unproxy(userObj);

        Classroom classroom = classroomRepository.findByRoomCode(code)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Sala de aula não encontrada com o código: " + code));

        if (!managedStudent.getClassrooms().contains(classroom)) {
            managedStudent.getClassrooms().add(classroom);
            userRepository.save(managedStudent);
        }
    }
}