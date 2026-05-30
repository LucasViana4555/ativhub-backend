package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.dto.ProfessorRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.StudentRegistrationRequest;
import com.example.AtivHub.AtivHub.domain.user.dto.OAuth2CompleteRegistrationDTO;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.infra.security.TokenService;
import com.example.AtivHub.AtivHub.repository.ClassroomRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("authService")
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    @Transactional
    public void registerProfessor(ProfessorRegistrationRequest data) {
        validateEmail(data.email());
        String encryptedPassword = passwordEncoder.encode(data.password());
        Professor professor = new Professor(data.name(), data.email(), encryptedPassword, data.schoolName(), data.subject());
        userRepository.save(professor);
    }

    @Transactional
    public void registerStudent(StudentRegistrationRequest data) {
        validateEmail(data.email());
        
        Classroom classroom = null;
        if (data.roomCode() != null && !data.roomCode().trim().isEmpty()) {
            classroom = classroomRepository.findByRoomCode(data.roomCode())
                    .orElseThrow(() -> new IllegalArgumentException("Código de sala inválido."));
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        Student student = new Student(data.name(), data.email(), encryptedPassword);
        if (classroom != null) {
            student.getClassrooms().add(classroom);
        }
        userRepository.save(student);
    }

    public boolean isStudentInClassroom(Authentication authentication, UUID classroomId) {
        Object principal = org.hibernate.Hibernate.unproxy(authentication.getPrincipal());
        if (!(principal instanceof Student student)) {
            return false;
        }
        var managedUser = userRepository.findById(student.getId()).orElse(null);
        if (managedUser instanceof Student managedStudent) {
            return managedStudent.getClassrooms().stream().anyMatch(c -> c.getId().equals(classroomId));
        }
        return false;
    }

    @Transactional
    public User completeOAuth2Registration(OAuth2CompleteRegistrationDTO data) {
        String emailFromToken = tokenService.validateRegisterToken(data.tempToken());
        if (emailFromToken == null || !emailFromToken.equalsIgnoreCase(data.email())) {
            throw new IllegalArgumentException("Token de registro inválido ou expirado.");
        }

        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        User user;
        if ("PROFESSOR".equalsIgnoreCase(data.role())) {
            if (data.schoolName() == null || data.schoolName().trim().isEmpty() ||
                    data.subject() == null || data.subject().trim().isEmpty()) {
                throw new IllegalArgumentException("Escola e disciplina são obrigatórios para professores.");
            }
            user = new Professor(data.name(), data.email(), randomPassword, data.schoolName(), data.subject());
        } else if ("STUDENT".equalsIgnoreCase(data.role()) || "ALUNO".equalsIgnoreCase(data.role())) {
            Classroom classroom = null;
            if (data.roomCode() != null && !data.roomCode().trim().isEmpty()) {
                classroom = classroomRepository.findByRoomCode(data.roomCode())
                        .orElseThrow(() -> new IllegalArgumentException("Código de sala inválido."));
            }
            Student s = new Student(data.name(), data.email(), randomPassword);
            if (classroom != null) {
                s.getClassrooms().add(classroom);
            }
            user = s;
        } else {
            throw new IllegalArgumentException("Role inválido. Deve ser PROFESSOR ou STUDENT/ALUNO.");
        }

        return userRepository.save(user);
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
    }
}