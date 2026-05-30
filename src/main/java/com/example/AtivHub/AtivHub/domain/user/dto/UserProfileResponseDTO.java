package com.example.AtivHub.AtivHub.domain.user.dto;

import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.classroom.dto.ClassroomResponseDTO;

import java.util.UUID;

import java.util.List;

public record UserProfileResponseDTO(
        UUID id,
        String name,
        String email,
        String role,
        Integer xp,
        String schoolName,
        String subject,
        List<ClassroomResponseDTO> classrooms
) {
    public UserProfileResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getXp(),
                org.hibernate.Hibernate.unproxy(user) instanceof Professor p ? p.getSchoolName() : null,
                org.hibernate.Hibernate.unproxy(user) instanceof Professor p ? p.getSubject() : null,
                org.hibernate.Hibernate.unproxy(user) instanceof Student s ? 
                        s.getClassrooms().stream().map(ClassroomResponseDTO::new).toList() : null
        );
    }
}