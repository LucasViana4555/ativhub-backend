package com.example.AtivHub.AtivHub.domain.classroom.dto;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import java.util.UUID;

public record ClassroomResponseDTO(
        UUID id,
        String name,
        String code,
        String teacherName,
        String subject
) {
    public ClassroomResponseDTO(Classroom classroom) {
        this(
                classroom.getId(),
                classroom.getName(),
                classroom.getRoomCode(),
                classroom.getTeacher().getName(),
                classroom.getTeacher().getSubject()
        );
    }
}