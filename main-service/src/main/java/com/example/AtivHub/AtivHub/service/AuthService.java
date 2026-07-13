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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.AtivHub.AtivHub.domain.user.event.StudentRegisteredEvent;
import com.example.AtivHub.AtivHub.domain.classroom.event.StudentJoinedClassroomEvent;
import java.util.UUID;

@Service("authService")
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailVerificationService emailVerificationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail informado."));
    }

    @Transactional
    public User registerProfessor(ProfessorRegistrationRequest data) {
        validateEmail(data.email());
        String encryptedPassword = passwordEncoder.encode(data.password());
        Professor professor = new Professor(data.name(), data.email(), encryptedPassword, data.schoolName(), data.subject());
        professor.setEmailVerified(false);
        return userRepository.save(professor);
    }

    @Transactional
    public User registerStudent(StudentRegistrationRequest data) {
        validateEmail(data.email());
        
        Classroom classroom = null;
        if (data.roomCode() != null && !data.roomCode().trim().isEmpty()) {
            classroom = classroomRepository.findByRoomCode(data.roomCode())
                    .orElseThrow(() -> new IllegalArgumentException("Código de sala inválido."));
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        Student student = new Student(data.name(), data.email(), encryptedPassword);
        student.setEmailVerified(false);
        if (classroom != null) {
            student.getClassrooms().add(classroom);
        }
        Student savedStudent = userRepository.save(student);

        try {
            eventPublisher.publishEvent(new StudentRegisteredEvent(savedStudent.getId(), savedStudent.getName(), savedStudent.getEmail()));
            
            if (classroom != null) {
                eventPublisher.publishEvent(new StudentJoinedClassroomEvent(
                    savedStudent.getId(), 
                    classroom.getId(), 
                    classroom.getName(), 
                    classroom.getTeacher().getSubject(), 
                    classroom.getRoomCode()
                ));
            }
        } catch (Exception e) {
            System.err.println("Erro ao publicar evento de registro: " + e.getMessage());
        }

        return savedStudent;
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

        User savedUser = userRepository.save(user);

        if (savedUser instanceof Student savedStudent) {
            try {
                eventPublisher.publishEvent(new StudentRegisteredEvent(savedStudent.getId(), savedStudent.getName(), savedStudent.getEmail()));
                
                if (!savedStudent.getClassrooms().isEmpty()) {
                    Classroom classroom = savedStudent.getClassrooms().get(0);
                    eventPublisher.publishEvent(new StudentJoinedClassroomEvent(
                        savedStudent.getId(), 
                        classroom.getId(), 
                        classroom.getName(), 
                        classroom.getTeacher().getSubject(), 
                        classroom.getRoomCode()
                    ));
                }
            } catch (Exception e) {
                System.err.println("Erro ao publicar evento de registro OAuth2: " + e.getMessage());
            }
        }

        return savedUser;
    }

    private void validateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
    }
}